package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.tls;

import java.io.OutputStream;

/**
 * @deprecated Migrate to the (D)TLS API in it.richkmeli.jframework.crypto.algorithm.bouncycastle.tls (bctls jar).
 */
public interface TlsCompression {
    OutputStream compress(OutputStream output);

    OutputStream decompress(OutputStream output);
}
