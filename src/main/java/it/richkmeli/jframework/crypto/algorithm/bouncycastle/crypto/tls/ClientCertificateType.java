package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.tls;

/**
 * @deprecated Migrate to the (D)TLS API in it.richkmeli.jframework.crypto.algorithm.bouncycastle.tls (bctls jar).
 */
public class ClientCertificateType {
    /*
     *  RFC 4346 7.4.4
     */
    public static final short rsa_sign = 1;
    public static final short dss_sign = 2;
    public static final short rsa_fixed_dh = 3;
    public static final short dss_fixed_dh = 4;
    public static final short rsa_ephemeral_dh_RESERVED = 5;
    public static final short dss_ephemeral_dh_RESERVED = 6;
    public static final short fortezza_dms_RESERVED = 20;

    /*
     * RFC 4492 5.5
     */
    public static final short ecdsa_sign = 64;
    public static final short rsa_fixed_ecdh = 65;
    public static final short ecdsa_fixed_ecdh = 66;
}
