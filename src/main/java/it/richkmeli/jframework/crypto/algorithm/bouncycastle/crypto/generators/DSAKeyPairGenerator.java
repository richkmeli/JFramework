package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.generators;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.AsymmetricCipherKeyPair;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.KeyGenerationParameters;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.DSAKeyGenerationParameters;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.DSAParameters;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.DSAPublicKeyParameters;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.math.ec.WNafUtil;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.util.BigIntegers;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * a DSA key pair generator.
 * <p>
 * This generates DSA keys in line with the method described
 * in <i>FIPS 186-3 B.1 FFC Key Pair Generation</i>.
 */
public class DSAKeyPairGenerator
        implements AsymmetricCipherKeyPairGenerator {
    private static final BigInteger ONE = BigInteger.valueOf(1);

    private DSAKeyGenerationParameters param;

    public void init(
            KeyGenerationParameters param) {
        this.param = (DSAKeyGenerationParameters) param;
    }

    public AsymmetricCipherKeyPair generateKeyPair() {
        DSAParameters dsaParams = param.getParameters();

        BigInteger x = generatePrivateKey(dsaParams.getQ(), param.getRandom());
        BigInteger y = calculatePublicKey(dsaParams.getP(), dsaParams.getG(), x);

        return new AsymmetricCipherKeyPair(
                new DSAPublicKeyParameters(y, dsaParams),
                new DSAPrivateKeyParameters(x, dsaParams));
    }

    private static BigInteger generatePrivateKey(BigInteger q, SecureRandom random) {
        // B.1.2 Key Pair Generation by Testing Candidates
        int minWeight = q.bitLength() >>> 2;
        for (; ; ) {
            // TODO Prefer this method? (change test cases that used fixed random)
            // B.1.1 Key Pair Generation Using Extra Random Bits
//            BigInteger x = new BigInteger(q.bitLength() + 64, random).mod(q.subtract(ONE)).add(ONE);

            BigInteger x = BigIntegers.createRandomInRange(ONE, q.subtract(ONE), random);
            if (WNafUtil.getNafWeight(x) >= minWeight) {
                return x;
            }
        }
    }

    private static BigInteger calculatePublicKey(BigInteger p, BigInteger g, BigInteger x) {
        return g.modPow(x, p);
    }
}
