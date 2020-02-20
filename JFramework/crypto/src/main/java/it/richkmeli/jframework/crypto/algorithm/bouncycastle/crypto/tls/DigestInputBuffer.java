package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.tls;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.Digest;

import java.io.ByteArrayOutputStream;

class DigestInputBuffer extends ByteArrayOutputStream {
    void updateDigest(Digest d) {
        d.update(this.buf, 0, count);
    }
}
