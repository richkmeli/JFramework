package org.bouncycastle.crypto.tls;

import org.bouncycastle.crypto.Signer;

import java.io.ByteArrayOutputStream;

class SignerInputBuffer extends ByteArrayOutputStream {
    void updateSigner(Signer s) {
        s.update(this.buf, 0, count);
    }
}