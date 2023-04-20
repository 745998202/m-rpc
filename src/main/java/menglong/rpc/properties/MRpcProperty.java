package menglong.rpc.properties;

public class MRpcProperty {
    // 服务注册中心
    private String registerAddress = "127.0.0.1:2181";

    // 服务暴露端口
    private Integer serverPort = 19000;

    // 服务协议
    private String protocol = "MRPC";

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getRegisterAddress() {
        return registerAddress;
    }

    public void setRegisterAddress(String registerAddress) {
        this.registerAddress = registerAddress;
    }
}
