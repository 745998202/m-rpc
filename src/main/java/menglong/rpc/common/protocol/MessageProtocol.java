package menglong.rpc.common.protocol;

/**
 * 消息协议，定义编组请求、解组请求、编组响应、解组响应
 */


public interface MessageProtocol {
    byte[] marshallingRequest(MRequest request) throws Exception;
    MRequest unmarshallingRequest(byte[] data) throws Exception;
    byte[] marshallingResponse(MResponse response)throws Exception;
    MResponse unmarshallingResponse(byte[] data)throws Exception;
}
