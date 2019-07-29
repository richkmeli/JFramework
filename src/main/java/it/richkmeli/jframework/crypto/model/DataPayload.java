package it.richkmeli.jframework.crypto.model;

public class DataPayload {
    // value encrypted with the session AES key
    private String encryptedData = null;
    // ID:
    // -    alphanumeric identifier of 16 elements: payload from the client
    // -    null: payload from the server
    private String id = null;

    public DataPayload(String encryptedData, String id) {
        this.encryptedData = encryptedData;
        this.id = id;
    }

    public String getEncryptedData() {
        return encryptedData;
    }

    public void setEncryptedData(String encryptedData) {
        this.encryptedData = encryptedData;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
