package menglong.rpc.common.serializer;

import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;

import java.nio.charset.StandardCharsets;

/**
 * Zookeeper序列化类，实现序列化与反序列化
 */
public class ZookeeperSerializer implements ZkSerializer {

    /**
     *
     */
    @Override
    public Object deserialize(byte[] bytes) throws ZkMarshallingError {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     *
     */
    @Override
    public byte[] serialize(Object obj) throws ZkMarshallingError {
        return String.valueOf(obj).getBytes(StandardCharsets.UTF_8);
    }
}
