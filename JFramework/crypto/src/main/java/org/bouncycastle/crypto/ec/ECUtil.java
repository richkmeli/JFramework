package org.bouncycastle.crypto.ec;

import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.util.BigIntegers;

import java.math.BigInteger;
import java.security.SecureRandom;

class ECUtil {
    static BigInteger generateK(BigInteger n, SecureRandom random) {
        int nBitLength = n.bitLength();
        BigInteger k;
        do {
            k = BigIntegers.createRandomBigInteger(nBitLength, random);
        }
        while (k.equals(ECConstants.ZERO) || (k.compareTo(n) >= 0));
        return k;
    }
}
