package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.tls;

/**
 * @deprecated Migrate to the (D)TLS API in it.richkmeli.jframework.crypto.algorithm.bouncycastle.tls (bctls jar).
 */
public class SupplementalDataEntry {
    protected int dataType;
    protected byte[] data;

    public SupplementalDataEntry(int dataType, byte[] data) {
        this.dataType = dataType;
        this.data = data;
    }

    public int getDataType() {
        return dataType;
    }

    public byte[] getData() {
        return data;
    }
}
