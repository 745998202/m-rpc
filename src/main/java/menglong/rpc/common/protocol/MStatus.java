package menglong.rpc.common.protocol;

public enum MStatus {
    SUCCESS(200, "SUCCESS"),
    ERROR(500, "ERROR"),
    NOT_FOUND(404, "NOT FOUND");

    private int code;
    private String message;
    MStatus(int code, String message){
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
