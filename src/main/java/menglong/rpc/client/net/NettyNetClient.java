package menglong.rpc.client.net;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import menglong.rpc.client.net.handler.SendHandler;
import menglong.rpc.common.service.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyNetClient implements NetClient{
    private static Logger logger = LoggerFactory.getLogger(NettyNetClient.class);

    /**
     * 客户端发送请求
     * @param data 请求的数据
     * @param service 请求的服务
     * @return 响应数据
     * @throws InterruptedException 异常
     */
    @Override
    public byte[] sendRequest(byte[] data, Service service) throws InterruptedException {
        String[] addInfoArray = service.getAddress().split(":");
        String serviceAddress = addInfoArray[0];
        String servicePort = addInfoArray[1];

        SendHandler sendHandler = new SendHandler(data);
        byte[] resData;

        // 配置客户端 NIO连接，创建Channel
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline p = socketChannel.pipeline();
                            p.addLast(sendHandler);
                        }
                    });
            // 启动客户端连接
            b.connect(serviceAddress, Integer.parseInt(servicePort)).sync();
            resData = (byte[]) sendHandler.rspData();
            logger.info("SendRequest get reply: {}", resData);
        } finally {
            // 释放线程资源
            group.shutdownGracefully();
        }

        return resData;
    }
}
