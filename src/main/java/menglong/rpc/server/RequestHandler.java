package menglong.rpc.server;

import menglong.rpc.common.protocol.MRequest;
import menglong.rpc.common.protocol.MResponse;
import menglong.rpc.common.protocol.MStatus;
import menglong.rpc.common.protocol.MessageProtocol;
import menglong.rpc.server.register.ServiceObject;
import menglong.rpc.server.register.ServiceRegister;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 服务处理者，提供编组请求，解组响应等操作
 */
public class RequestHandler {
    private MessageProtocol protocol;
    private ServiceRegister serviceRegister;

    public RequestHandler(MessageProtocol protocol, ServiceRegister serviceRegister){
        super();
        this.protocol = protocol;
        this.serviceRegister = serviceRegister;
    }

    public byte[] handleRequest(byte[] data) throws Exception{
        // 解组请求
        MRequest req = this.protocol.unmarshallingRequest(data);
        // 查找服务对象
        ServiceObject so = this.serviceRegister.getServiceObject(req.getServiceName());
        MResponse rsp = null;
        if(so == null){
            rsp = new MResponse(MStatus.NOT_FOUND);
        }else{
            // 3.反射调用对应方法
            try{
                Method m = so.getClazz().getMethod(req.getMethod(), req.getParameterTypes());
                Object returnValue = m.invoke(so.getObj(),req.getParameters());
                rsp.setReturnValue(returnValue);
            }catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e){
                rsp = new MResponse(MStatus.ERROR);
                rsp.setException(e);
            }
        }
        // 编组响应请求
        return this.protocol.marshallingResponse(rsp);
    }

    public MessageProtocol getProtocol() {
        return protocol;
    }

    public void setProtocol(MessageProtocol protocol) {
        this.protocol = protocol;
    }

    public ServiceRegister getServiceRegister() {
        return serviceRegister;
    }

    public void setServiceRegister(ServiceRegister serviceRegister) {
        this.serviceRegister = serviceRegister;
    }
}
