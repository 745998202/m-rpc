package menglong.rpc.client.discovery;

import com.alibaba.fastjson.JSON;
import menglong.rpc.common.constants.Constant;
import menglong.rpc.common.serializer.ZookeeperSerializer;
import menglong.rpc.common.service.Service;
import org.I0Itec.zkclient.ZkClient;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ZooKeeperServiceDiscoverer implements ServiceDiscoverer{

    private ZkClient zkClient;

    public ZooKeeperServiceDiscoverer(String zkAddress){
        // 创建zookeep连接
        zkClient = new ZkClient(zkAddress);
        // 设置序列化实现
        zkClient.setZkSerializer(new ZookeeperSerializer());
    }

    /**
     * 使用Zookeeper客户端，通过服务名称获取服务列表
     * 服务名称格式：接口全路径
     * 获取服务名对应的所有服务Service
     * @param name
     * @return
     */
    @Override
    public List<Service> getService(String name) {
        String servicePath = Constant.ZK_SERVICE_PATH + Constant.PATH_DELIMITER + name + "/services";
        List<String> children = zkClient.getChildren(servicePath);
        // Optional使用
        return Optional.ofNullable(children).orElse(new ArrayList<>()).stream().map(str -> {
            String deCh = null;
            try{
                deCh = URLDecoder.decode(str, Constant.UTF_8);
            }catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }
            return JSON.parseObject(deCh, Service.class);
        }).collect(Collectors.toList());
    }
}
