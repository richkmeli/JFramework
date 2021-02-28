package org.bouncycastle.crypto.tls;

import org.bouncycastle.crypto.Digest;

import java.io.ByteArrayOutputStream;

class DigestInputBuffer extends ByteArrayOutputStream {
    void updateDigest(Digest d) {
        d.update(this.buf, 0, count);
    }
}
