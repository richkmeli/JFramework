package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.tls;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.Signer;

import java.io.ByteArrayOutputStream;

class SignerInputBuffer extends ByteArrayOutputStream {
    void updateSigner(Signer s) {
        s.update(this.buf, 0, count);
    }
}