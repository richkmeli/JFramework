package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.io;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.Digest;

import java.io.IOException;
import java.io.OutputStream;

public class DigestOutputStream
        extends OutputStream {
    protected Digest digest;

    public DigestOutputStream(
            Digest Digest) {
        this.digest = Digest;
    }

    public void write(int b)
            throws IOException {
        digest.update((byte) b);
    }

    public void write(
            byte[] b,
            int off,
            int len)
            throws IOException {
        digest.update(b, off, len);
    }

    public byte[] getDigest() {
        byte[] res = new byte[digest.getDigestSize()];

        digest.doFinal(res, 0);

        return res;
    }
}
