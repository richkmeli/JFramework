package it.richkmeli.jframework.network.tcp.server.http.payload.response;

public class StatusCode {
    private Integer code;
    private String message;

    public StatusCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}