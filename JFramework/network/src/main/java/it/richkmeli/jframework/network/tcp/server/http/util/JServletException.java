package it.richkmeli.jframework.network.tcp.server.http.util;

import it.richkmeli.jframework.network.tcp.server.http.payload.response.BaseStatusCode;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.KoResponse;
import it.richkmeli.jframework.network.tcp.server.http.payload.response.Response;

public class JServletException extends Exception {
    private KoResponse koResponse;

    public JServletException(Exception exception) {
        super(exception);
    }

    public JServletException(String errorMessage) {
        super(errorMessage);
    }

    public JServletException(KoResponse koResponse) {
        this.koResponse = koResponse;
    }

    public String getKoResponseJSON() {
        return getResponse().json();
    }

    public Response getResponse() {
        if(koResponse != null) {
            return koResponse;
        }else {
            return new KoResponse(BaseStatusCode.GENERIC_ERROR, super.getMessage());
        }
    }

}