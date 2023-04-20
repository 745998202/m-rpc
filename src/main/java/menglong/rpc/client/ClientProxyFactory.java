package menglong.rpc.client;

import menglong.rpc.client.discovery.ServiceDiscoverer;
import menglong.rpc.client.net.NetClient;
import menglong.rpc.common.protocol.MRequest;
import menglong.rpc.common.protocol.MResponse;
import menglong.rpc.common.protocol.MessageProtocol;
import menglong.rpc.common.service.Service;
import menglong.rpc.exception.MException;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.lang.reflect.InvocationHandler;
import java.util.Random;

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


    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz){
        // 将生成的代理对象存储在Map中
        return (T)this.objectCatch.computeIfAbsent(clazz,
                cls -> newProxyInstance(cls.getClassLoader(), new Class<?>[]{cls}, new ClientInvocationHandler(cls)));
    }

    public ServiceDiscoverer getServiceDiscoverer() {
        return serviceDiscoverer;
    }

    public void setSid(ServiceDiscoverer serviceDiscoverer){this.serviceDiscoverer = serviceDiscoverer;}

    public NetClient getNetClient() {
        return netClient;
    }

    public void setNetClient(NetClient netClient) {
        this.netClient = netClient;
    }

    public Map<String, MessageProtocol> getSupportMessageProtocols() {
        return supportMessageProtocols;
    }

    public void setSupportMessageProtocols(Map<String, MessageProtocol> supportMessageProtocols) {
        this.supportMessageProtocols = supportMessageProtocols;
    }

    private class ClientInvocationHandler implements InvocationHandler{
        private Class<?> clazz;
        private Random random = new Random();
        public ClientInvocationHandler(Class<?> clazz){
            super();
            this.clazz = clazz;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if(method.getName().equals("toString")){
                return proxy.getClass().toString();
            }
            if(method.getName().equals("hashCode")){
                return 0;
            }

            // 1. 获取服务信息
            String serviceName = this.clazz.getName();
            List<Service> services = serviceDiscoverer.getService(serviceName);

            if(services == null || services.isEmpty()){
                throw new MException("No provider available!");
            }

            // 随机选择一个服务提供者（软负载均衡）
            Service service = services.get(random.nextInt(services.size()));

            // 2. 构建一个Request对象
            MRequest req = new MRequest();
            req.setServiceName(service.getName());
            req.setMethod(method.getName());
            req.setParameterTypes(method.getParameterTypes());
            req.setParameters(args);

            // 3. 协议层编组
            // 获得该方法对应的协议
            MessageProtocol protocol = supportMessageProtocols.get(service.getProtocol());
            // 编组请求
            byte[] data = protocol.marshallingRequest(req);

            // 4. 调用网络层发送请求
            byte[] repData = netClient.sendRequest(data, service);

            // 5. 解组响应
            MResponse res = protocol.unmarshallingResponse(repData);

            // 6. 结果处理
            if(res.getException() != null){
                throw res.getException();
            }

            return res.getReturnValue();

        }
    }

}
