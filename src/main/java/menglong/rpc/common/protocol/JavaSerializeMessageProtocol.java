package menglong.rpc.common.protocol;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

public class JavaSerializeMessageProtocol implements MessageProtocol{
    private byte[] serialize(Object obj) throws Exception{
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bout);
        out.writeObject(obj);
        return bout.toByteArray();
    }

    // TODO
    @Override
    public byte[] marshallingRequest(MRequest request) throws Exception {
        return new byte[0];
    }

    @Override
    public MRequest unmarshallingRequest(byte[] data) throws Exception {
        return null;
    }

    @Override
    public byte[] marshallingResponse(MResponse response) throws Exception {
        return new byte[0];
    }

    @Override
    public MResponse unmarshallingResponse(byte[] data) throws Exception {
        return null;
    }
}
