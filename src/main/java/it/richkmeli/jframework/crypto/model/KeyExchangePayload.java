package it.richkmeli.jframework.crypto.model;


public class KeyExchangePayload extends SessionKeyPayload {
    private String publicClientKey = null;


    public KeyExchangePayload(String sessionAESKey, String signatureSessionAESKey, String id, String publicClientKey) {
        super(sessionAESKey, signatureSessionAESKey, id);
        this.publicClientKey = publicClientKey;
    }

    public String getPublicClientKey() {
        return publicClientKey;
    }

    public void setPublicClientKey(String publicClientKey) {
        this.publicClientKey = publicClientKey;
    }
}


