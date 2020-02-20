package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.tls;

import java.io.IOException;

/**
 * @deprecated Migrate to the (D)TLS API in it.richkmeli.jframework.crypto.algorithm.bouncycastle.tls (bctls jar).
 */
public class AbstractTlsCipherFactory
        implements TlsCipherFactory {
    public TlsCipher createCipher(TlsContext context, int encryptionAlgorithm, int macAlgorithm)
            throws IOException {
        throw new TlsFatalAlert(AlertDescription.internal_error);
    }
}
