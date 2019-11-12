package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.tls;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.util.Arrays;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.util.Strings;

/**
 * @deprecated Migrate to the (D)TLS API in it.richkmeli.jframework.crypto.algorithm.bouncycastle.tls (bctls jar).
 */
public class BasicTlsPSKIdentity
        implements TlsPSKIdentity {
    protected byte[] identity;
    protected byte[] psk;

    public BasicTlsPSKIdentity(byte[] identity, byte[] psk) {
        this.identity = Arrays.clone(identity);
        this.psk = Arrays.clone(psk);
    }

    public BasicTlsPSKIdentity(String identity, byte[] psk) {
        this.identity = Strings.toUTF8ByteArray(identity);
        this.psk = Arrays.clone(psk);
    }

    public void skipIdentityHint() {
    }

    public void notifyIdentityHint(byte[] psk_identity_hint) {
    }

    public byte[] getPSKIdentity() {
        return identity;
    }

    public byte[] getPSK() {
        return Arrays.clone(psk);
    }
}
