package it.richkmeli.jframework.crypto.model;

public class SessionKeyPayload {
    private String sessionAESKey = null;
    private String signatureSessionAESKey = null;
    private String id = null;

    public SessionKeyPayload(String sessionAESKey, String signatureSessionAESKey, String id) {
        this.sessionAESKey = sessionAESKey;
        this.signatureSessionAESKey = signatureSessionAESKey;
        this.id = id;
    }

    public String getSessionAESKey() {
        return sessionAESKey;
    }

    public void setSessionAESKey(String sessionAESKey) {
        this.sessionAESKey = sessionAESKey;
    }

    public String getSignatureSessionAESKey() {
        return signatureSessionAESKey;
    }

    public void setSignatureSessionAESKey(String signatureSessionAESKey) {
        this.signatureSessionAESKey = signatureSessionAESKey;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
