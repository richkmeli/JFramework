package it.richkmeli.jframework.network.tcp.server.http.payload.response;

public class KoResponse extends Response {

    public KoResponse(StatusCode statusCode) {
        super("KO", statusCode);
    }

    public KoResponse(StatusCode statusCode, String additionalMessage) {
        super("KO", statusCode, additionalMessage);
    }

}
