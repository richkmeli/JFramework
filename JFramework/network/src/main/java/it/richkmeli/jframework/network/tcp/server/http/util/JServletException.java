package it.richkmeli.jframework.network.tcp.server.http.util;

import it.richkmeli.jframework.network.tcp.server.http.payload.response.KOResponse;

public class JServletException extends Exception {
    private KOResponse koResponse;

    public JServletException(Exception exception) {
        super(exception);
    }

    public JServletException(KOResponse koResponse) {
        this.koResponse = koResponse;
    }

    public String getKOResponseJSON() {
        return koResponse.json();
    }

    public KOResponse getKOResponse() {
        return koResponse;
    }
}