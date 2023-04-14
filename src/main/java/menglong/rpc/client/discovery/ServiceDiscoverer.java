package menglong.rpc.client.discovery;

import menglong.rpc.common.service.Service;

import java.util.List;

/**
 * 服务发现抽象类
 * @author menglong
 * @version 1.0
 */
public interface ServiceDiscoverer {
    List<Service> getService(String name);
}
