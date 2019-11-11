package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.generators;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.DHParameters;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.math.ec.WNafUtil;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.util.BigIntegers;

import java.math.BigInteger;
import java.security.SecureRandom;

class DHKeyGeneratorHelper {
    static final DHKeyGeneratorHelper INSTANCE = new DHKeyGeneratorHelper();

    private static final BigInteger ONE = BigInteger.valueOf(1);
    private static final BigInteger TWO = BigInteger.valueOf(2);

    private DHKeyGeneratorHelper() {
    }

    BigInteger calculatePrivate(DHParameters dhParams, SecureRandom random) {
        int limit = dhParams.getL();

        if (limit != 0) {
            int minWeight = limit >>> 2;
            for (; ; ) {
                BigInteger x = BigIntegers.createRandomBigInteger(limit, random).setBit(limit - 1);
                if (WNafUtil.getNafWeight(x) >= minWeight) {
                    return x;
                }
            }
        }

        BigInteger min = TWO;
        int m = dhParams.getM();
        if (m != 0) {
            min = ONE.shiftLeft(m - 1);
        }

        BigInteger q = dhParams.getQ();
        if (q == null) {
            q = dhParams.getP();
        }
        BigInteger max = q.subtract(TWO);

        int minWeight = max.bitLength() >>> 2;
        for (; ; ) {
            BigInteger x = BigIntegers.createRandomInRange(min, max, random);
            if (WNafUtil.getNafWeight(x) >= minWeight) {
                return x;
            }
        }
    }

    BigInteger calculatePublic(DHParameters dhParams, BigInteger x) {
        return dhParams.getG().modPow(x, dhParams.getP());
    }
}
