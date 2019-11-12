package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.modes.kgcm;

public interface KGCMMultiplier {
    void init(long[] H);

    void multiplyH(long[] z);
}
