package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.tls;

import java.io.IOException;

/**
 * @deprecated Migrate to the (D)TLS API in it.richkmeli.jframework.crypto.algorithm.bouncycastle.tls (bctls jar).
 */
public interface TlsCloseable {
    public void close() throws IOException;
}
