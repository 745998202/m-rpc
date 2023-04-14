package menglong.rpc.client.net.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;


/**
 * 发送处理类，定义Netty入站处理规则
 * @author menglong
 * @version 1.0
 */
public class SendHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(SendHandler.class);
    private CountDownLatch cdl;
    private Object readMsg = null;
    private byte[] data;

    public SendHandler(byte[] data){
        cdl = new CountDownLatch(1);
        this.data = data;
    }

    /**
     * 当成功连接服务端之后，发送请求
     * @param ctx 通道上下文
     * @throws Exception 异常
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Successful connection to server: {}", ctx);
        // 创建Buffer缓冲区
        ByteBuf reqBuf = Unpooled.buffer(data.length);
        // 将请求数据写入缓冲区
        reqBuf.writeBytes(data);
        logger.info("Client Send Message: {}", reqBuf);
        // 发送请求数据
        ctx.writeAndFlush(reqBuf);
    }

    /**
     * 读取数据，数据读取完毕之后释放CD锁
     * @param ctx 通道上下文
     * @param msg ByteBuf
     * @throws Exception 异常
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("Client reads message: {}", msg);
        ByteBuf msgBuf = (ByteBuf) msg;
        byte[] resp = new byte[msgBuf.readableBytes()];
        msgBuf.readBytes(resp);
        readMsg = resp;
        cdl.countDown();
    }

    /**
     * 等待数据读取完成
     * @return 响应数据
     * @throws InterruptedException 线程中断异常
     */
    public Object rspData() throws InterruptedException{
        cdl.await();
        return readMsg;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        logger.error("Exception occurred: {}", cause.getMessage());
        ctx.close();
    }
}
