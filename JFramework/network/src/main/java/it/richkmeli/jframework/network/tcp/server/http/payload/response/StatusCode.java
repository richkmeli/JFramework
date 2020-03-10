package it.richkmeli.jframework.network.tcp.server.http.payload.response;

import javafx.util.Pair;

public class StatusCode extends Pair<Integer,String> {

    public StatusCode(Integer key, String value) {
        super(key, value);
    }

    public String getMessage() {
        return getValue();
    }

    public int getCode() {
        return getKey();
    }
}