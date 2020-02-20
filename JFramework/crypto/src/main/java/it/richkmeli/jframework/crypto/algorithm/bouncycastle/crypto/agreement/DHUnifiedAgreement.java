package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.agreement;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.CipherParameters;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.DHUPrivateParameters;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.DHUPublicParameters;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.util.Arrays;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.util.BigIntegers;

import java.math.BigInteger;

/**
 * FFC Unified static/ephemeral agreement as described in NIST SP 800-56A.
 */
public class DHUnifiedAgreement {
    private DHUPrivateParameters privParams;

    public void init(
            CipherParameters key) {
        this.privParams = (DHUPrivateParameters) key;
    }

    public int getFieldSize() {
        return (privParams.getStaticPrivateKey().getParameters().getP().bitLength() + 7) / 8;
    }

    public byte[] calculateAgreement(CipherParameters pubKey) {
        DHUPublicParameters pubParams = (DHUPublicParameters) pubKey;

        DHBasicAgreement sAgree = new DHBasicAgreement();
        DHBasicAgreement eAgree = new DHBasicAgreement();

        sAgree.init(privParams.getStaticPrivateKey());

        BigInteger sComp = sAgree.calculateAgreement(pubParams.getStaticPublicKey());

        eAgree.init(privParams.getEphemeralPrivateKey());

        BigInteger eComp = eAgree.calculateAgreement(pubParams.getEphemeralPublicKey());

        return Arrays.concatenate(
                BigIntegers.asUnsignedByteArray(this.getFieldSize(), eComp),
                BigIntegers.asUnsignedByteArray(this.getFieldSize(), sComp));
    }
}
