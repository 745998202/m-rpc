package menglong.rpc.client;

import menglong.rpc.client.discovery.ServiceDiscoverer;
import menglong.rpc.client.net.NetClient;
import menglong.rpc.common.protocol.MessageProtocol;

import java.util.HashMap;
import java.util.Map;

import static java.lang.reflect.Proxy.newProxyInstance;
/**
 * 客户端代理工厂：用于创建远程服务代理类
 * 封装编组请求，请求发送，编组响应等操作
 */
public class ClientProxyFactory {
    private ServiceDiscoverer serviceDiscoverer;
    private Map<String, MessageProtocol> supportMessageProtocols;
    private NetClient netClient;
    private Map<Class<?>, Object> objectCatch = new HashMap<>();

    public <T> T getProxy(Class<T> clazz){
        return (T)this.objectCatch.computeIfAbsent(clazz,
                cls -> newProxyInstance(cls.getClassLoader(), new Class<?>[]{cls}, new ClientProxyFactory(cls)));
    }

}
