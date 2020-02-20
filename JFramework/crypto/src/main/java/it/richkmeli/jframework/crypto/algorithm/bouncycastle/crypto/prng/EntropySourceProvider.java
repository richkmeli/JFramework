package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.prng;

public interface EntropySourceProvider {
    EntropySource get(final int bitsRequired);
}
