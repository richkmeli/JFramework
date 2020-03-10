package it.richkmeli.jframework.network.tcp.server.http.util;

import it.richkmeli.jframework.network.tcp.server.http.payload.response.KoResponse;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.Response;

public class JServletException extends Exception {
    private KoResponse koResponse;

    public JServletException(Exception exception) {
        super(exception);
    }

    public JServletException(KoResponse koResponse) {
        this.koResponse = koResponse;
    }

    public String getKoResponseJSON() {
        return koResponse.json();
    }

    public Response getResponse() {
        return koResponse;
    }

}