package it.richkmeli.jframework.network.tcp.server.http.payload.response;

public class OKResponse extends BasicResponse {

    public OKResponse(StatusCode statusCode, String message) {
        setStatus("OK");
        setStatusCode(statusCode);
        setMessage(message);
    }

    public OKResponse(StatusCode statusCode) {
        this(statusCode, null);
    }

}
