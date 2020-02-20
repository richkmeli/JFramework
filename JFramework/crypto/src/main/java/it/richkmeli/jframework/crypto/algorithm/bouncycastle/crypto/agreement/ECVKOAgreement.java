package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.agreement;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.CipherParameters;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.Digest;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.ECDomainParameters;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.ECPrivateKeyParameters;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.ECPublicKeyParameters;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.ParametersWithUKM;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.math.ec.ECAlgorithms;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.math.ec.ECPoint;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.util.BigIntegers;

import java.math.BigInteger;

/**
 * GOST VKO key agreement class - RFC 7836 Section 4.3
 */
public class ECVKOAgreement {
    private final Digest digest;

    private ECPrivateKeyParameters key;
    private BigInteger ukm;

    public ECVKOAgreement(Digest digest) {
        this.digest = digest;
    }

    public void init(
            CipherParameters key) {
        ParametersWithUKM p = (ParametersWithUKM) key;

        this.key = (ECPrivateKeyParameters) p.getParameters();
        this.ukm = toInteger(p.getUKM());
    }

    public int getFieldSize() {
        return (key.getParameters().getCurve().getFieldSize() + 7) / 8;
    }

    public byte[] calculateAgreement(
            CipherParameters pubKey) {
        ECPublicKeyParameters pub = (ECPublicKeyParameters) pubKey;
        ECDomainParameters params = key.getParameters();
        if (!params.equals(pub.getParameters())) {
            throw new IllegalStateException("ECVKO public key has wrong domain parameters");
        }

        BigInteger hd = params.getH().multiply(ukm).multiply(key.getD()).mod(params.getN());

        // Always perform calculations on the exact curve specified by our private key's parameters
        ECPoint pubPoint = ECAlgorithms.cleanPoint(params.getCurve(), pub.getQ());
        if (pubPoint.isInfinity()) {
            throw new IllegalStateException("Infinity is not a valid public key for ECDHC");
        }

        ECPoint P = pubPoint.multiply(hd).normalize();

        if (P.isInfinity()) {
            throw new IllegalStateException("Infinity is not a valid agreement value for ECVKO");
        }

        return fromPoint(P);
    }

    private static BigInteger toInteger(byte[] ukm) {
        byte[] v = new byte[ukm.length];

        for (int i = 0; i != v.length; i++) {
            v[i] = ukm[ukm.length - i - 1];
        }

        return new BigInteger(1, v);
    }

    private byte[] fromPoint(ECPoint v) {
        BigInteger bX = v.getAffineXCoord().toBigInteger();
        BigInteger bY = v.getAffineYCoord().toBigInteger();

        int size;
        if (bX.toByteArray().length > 33) {
            size = 64;
        } else {
            size = 32;
        }

        byte[] bytes = new byte[2 * size];
        byte[] x = BigIntegers.asUnsignedByteArray(size, bX);
        byte[] y = BigIntegers.asUnsignedByteArray(size, bY);

        for (int i = 0; i != size; i++) {
            bytes[i] = x[size - i - 1];
        }
        for (int i = 0; i != size; i++) {
            bytes[size + i] = y[size - i - 1];
        }

        digest.update(bytes, 0, bytes.length);

        byte[] rv = new byte[digest.getDigestSize()];

        digest.doFinal(rv, 0);

        return rv;
    }
}
