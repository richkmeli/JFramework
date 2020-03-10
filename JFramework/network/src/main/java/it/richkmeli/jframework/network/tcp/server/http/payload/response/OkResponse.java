package it.richkmeli.jframework.network.tcp.server.http.payload.response;

/**
 * Success status code has no message, so additionlMessage is set as is message
 */

public class OkResponse extends Response{

    public OkResponse(StatusCode statusCode, String additionalMessage) {
        super("OK",statusCode,additionalMessage);

    }

}
