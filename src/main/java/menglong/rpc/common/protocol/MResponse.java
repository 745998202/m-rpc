package menglong.rpc.common.protocol;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 响应信息封装类
 */
public class MResponse implements Serializable {
    private static final long serialVersionUID = -4317845782629589997L;

    private MStatus status;

    private Map<String, String> headers = new HashMap<>();

    private Object returnValue;

    private Exception exception;

    public MResponse(MStatus status) {
        this.status = status;
    }

    public void setStatus(MStatus status) {
        this.status = status;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public MStatus getStatus() {
        return status;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public Exception getException() {
        return exception;
    }

    public String getHeader(String name) {
        return this.headers == null ? null : this.headers.get(name);
    }

    public void setHeader(String name, String value) {
        this.headers.put(name, value);

    }
}
