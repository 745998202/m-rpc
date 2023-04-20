package menglong.rpc.server;

/**
 * 服务器端口 抽象类
 */
public abstract class RpcServer {
    // 服务端口
    protected int port;

    // 服务协议
    protected String protocol;

    // 请求处理者
    protected RequestHandler handler;

    public RpcServer(int port, String protocol, RequestHandler handler){
        super();
        this.port = port;
        this.protocol = protocol;
        this.handler = handler;
    }

    public abstract void start();
    public abstract void stop();

}
