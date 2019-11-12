package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.generators;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.AsymmetricCipherKeyPair;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.CryptoServicesRegistrar;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.KeyGenerationParameters;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.ECDomainParameters;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.ECKeyGenerationParameters;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.ECPrivateKeyParameters;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.ECPublicKeyParameters;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.math.ec.*;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.util.BigIntegers;

import java.math.BigInteger;
import java.security.SecureRandom;

public class ECKeyPairGenerator
        implements AsymmetricCipherKeyPairGenerator, ECConstants {
    ECDomainParameters params;
    SecureRandom random;

    public void init(
            KeyGenerationParameters param) {
        ECKeyGenerationParameters ecP = (ECKeyGenerationParameters) param;

        this.random = ecP.getRandom();
        this.params = ecP.getDomainParameters();

        if (this.random == null) {
            this.random = CryptoServicesRegistrar.getSecureRandom();
        }
    }

    /**
     * Given the domain parameters this routine generates an EC key
     * pair in accordance with X9.62 section 5.2.1 pages 26, 27.
     */
    public AsymmetricCipherKeyPair generateKeyPair() {
        BigInteger n = params.getN();
        int nBitLength = n.bitLength();
        int minWeight = nBitLength >>> 2;

        BigInteger d;
        for (; ; ) {
            d = BigIntegers.createRandomBigInteger(nBitLength, random);

            if (d.compareTo(ONE) < 0 || (d.compareTo(n) >= 0)) {
                continue;
            }

            if (WNafUtil.getNafWeight(d) < minWeight) {
                continue;
            }

            break;
        }

        ECPoint Q = createBasePointMultiplier().multiply(params.getG(), d);

        return new AsymmetricCipherKeyPair(
                new ECPublicKeyParameters(Q, params),
                new ECPrivateKeyParameters(d, params));
    }

    protected ECMultiplier createBasePointMultiplier() {
        return new FixedPointCombMultiplier();
    }
}
