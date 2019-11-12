package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.tls;

/**
 * @deprecated Migrate to the (D)TLS API in it.richkmeli.jframework.crypto.algorithm.bouncycastle.tls (bctls jar).
 */
public class ClientAuthenticationType {
    /*
     * RFC 5077 4
     */
    public static final short anonymous = 0;
    public static final short certificate_based = 1;
    public static final short psk = 2;
}
