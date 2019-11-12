package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.prng;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.prng.drbg.SP80090DRBG;

interface DRBGProvider {
    SP80090DRBG get(EntropySource entropySource);
}
