package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.modes.gcm;

public interface GCMMultiplier {
    void init(byte[] H);

    void multiplyH(byte[] x);
}
