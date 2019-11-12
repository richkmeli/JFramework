package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.tls;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.AsymmetricKeyParameter;

import java.io.IOException;

/**
 * @deprecated Migrate to the (D)TLS API in it.richkmeli.jframework.crypto.algorithm.bouncycastle.tls (bctls jar).
 */
public interface TlsAgreementCredentials
        extends TlsCredentials {
    byte[] generateAgreement(AsymmetricKeyParameter peerPublicKey)
            throws IOException;
}
