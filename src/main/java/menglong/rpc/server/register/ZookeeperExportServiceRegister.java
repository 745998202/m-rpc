package menglong.rpc.server.register;

import com.alibaba.fastjson.JSON;
import menglong.rpc.common.service.Service;
import menglong.rpc.common.serializer.ZookeeperSerializer;
import org.I0Itec.zkclient.ZkClient;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;

import static menglong.rpc.common.constants.Constant.*;

/**
 * ZooKeeper服务注册器
 * 将服务序列化之后保存到Zookeeper上，供客户端发现
 * 将服务对象缓存起来，在服务端调用的时候，通过缓存的ServiceObject调用服务
 */
public class ZookeeperExportServiceRegister extends DefaultServiceRegister implements ServiceRegister{
    /**
     * ZooKeeper客户端
     */
    private ZkClient client;
    public ZookeeperExportServiceRegister(String zkAddress, Integer port, String protocol){
        client = new ZkClient(zkAddress);
        client.setZkSerializer(new ZookeeperSerializer());
        this.port = port;
        this.protocol = protocol;
    }

    /**
     * 服务注册
     * @param so 服务持有者
     * @throws Exception 注册异常
     */
    @Override
    public void register(ServiceObject so) throws Exception {
        super.register(so);
        Service service = new Service();
        String host = InetAddress.getLocalHost().getHostAddress();
        String address = host + ":" + port;
        service.setAddress(address);
        service.setName(so.getClazz().getName());
        service.setProtocol(protocol);
        this.exportService(service);
    }

    /**
     * 服务暴露
     * @param serviceResource 需要暴露的服务
     */
    private void exportService(Service serviceResource){
        String serviceName = serviceResource.getName();
        String uri = JSON.toJSONString(serviceResource);
        try{
            uri = URLEncoder.encode(uri, UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String servicePath = ZK_SERVICE_PATH + PATH_DELIMITER + serviceName + "/service";
        if(!client.exists(servicePath)){
            client.createPersistent(servicePath, true);
        }
        String uriPath = servicePath + PATH_DELIMITER + uri;
        if(client.exists(uriPath)){
            client.delete(uriPath);
        }
        client.createEphemeral(uriPath);
    }


}
