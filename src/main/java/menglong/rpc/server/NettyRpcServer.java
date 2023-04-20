package menglong.rpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class NettyRpcServer extends RpcServer{
    private static Logger logger = LoggerFactory.getLogger(NettyRpcServer.class);
    private Channel channel;
    public NettyRpcServer(int port, String protocol, RequestHandler handler){
        super(port, protocol, handler);
    }

    /**
     * 开始服务
     */
    @Override
    public void start() {
        // 配置服务器
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO)).childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline p = socketChannel.pipeline();
                            p.addLast(new ChannelRequestHandler());
                        }
                    });

            // 启动服务
            ChannelFuture f = b.bind(port).sync();
            logger.info("Server started successfully.");
            channel = f.channel();
            // 等待服务通道关闭
            f.channel().closeFuture().sync();
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            // 释放线程组资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void stop() {

    }

    private class ChannelRequestHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            logger.info("Channel active: {}", ctx);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            logger.info("The server receives a message: {}", msg);
            ByteBuf msgBuf =(ByteBuf)msg;
            byte[] req = new byte[msgBuf.readableBytes()];
            msgBuf.readBytes(req);
            byte[] res = handler.handleRequest(req);
            logger.info("Send response: {}", msg);
            ByteBuf respBuf = Unpooled.buffer(res.length);
            respBuf.writeBytes(res);
            ctx.write(respBuf);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            logger.error("Exception occured: {}", cause.getMessage());
            ctx.close();
        }
    }
}
