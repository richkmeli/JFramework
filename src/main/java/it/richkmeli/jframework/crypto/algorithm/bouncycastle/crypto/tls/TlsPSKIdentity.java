package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.tls;

/**
 * @deprecated Migrate to the (D)TLS API in it.richkmeli.jframework.crypto.algorithm.bouncycastle.tls (bctls jar).
 */
public interface TlsPSKIdentity {
    void skipIdentityHint();

    void notifyIdentityHint(byte[] psk_identity_hint);

    byte[] getPSKIdentity();

    byte[] getPSK();
}
