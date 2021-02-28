package org.bouncycastle.crypto.tls;

/**
 * @deprecated Migrate to the (D)TLS API in org.bouncycastle.tls (bctls jar).
 */
public class ClientAuthenticationType {
    /*
     * RFC 5077 4
     */
    public static final short anonymous = 0;
    public static final short certificate_based = 1;
    public static final short psk = 2;
}
