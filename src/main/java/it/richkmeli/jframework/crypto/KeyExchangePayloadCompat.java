package it.richkmeli.jframework.crypto;

@Deprecated
public class KeyExchangePayloadCompat {

    private String encryptedAESsecretKey = null;
    private String signatureAESsecretKey = null;
    private String kpubServer = null;
    private String data = null;

    public KeyExchangePayloadCompat(String encryptedAESsecretKey, String signatureAESsecretKey, String kpubServer, String data) {
        this.encryptedAESsecretKey = encryptedAESsecretKey;
        this.signatureAESsecretKey = signatureAESsecretKey;
        this.kpubServer = kpubServer;
        this.data = data;
    }

    public String getEncryptedAESsecretKey() {
        return encryptedAESsecretKey;
    }

    public String getKpubServer() {
        return kpubServer;
    }

    public String getSignatureAESsecretKey() {
        return signatureAESsecretKey;
    }

    public String getData() {
        return data;
    }

    public void setSignatureAESsecretKey(String signatureAESsecretKey) {
        this.signatureAESsecretKey = signatureAESsecretKey;
    }

    public void setEncryptedAESsecretKey(String encryptedAESsecretKey) {
        this.encryptedAESsecretKey = encryptedAESsecretKey;
    }

    public void setKpubServer(String kpubServer) {
        this.kpubServer = kpubServer;
    }

    public void setData(String data) {
        this.data = data;
    }
}