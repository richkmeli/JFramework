package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.tls;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.DSA;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.AsymmetricKeyParameter;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.ECPublicKeyParameters;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.signers.ECDSASigner;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.signers.HMacDSAKCalculator;

/**
 * @deprecated Migrate to the (D)TLS API in it.richkmeli.jframework.crypto.algorithm.bouncycastle.tls (bctls jar).
 */
public class TlsECDSASigner
        extends TlsDSASigner {
    public boolean isValidPublicKey(AsymmetricKeyParameter publicKey) {
        return publicKey instanceof ECPublicKeyParameters;
    }

    protected DSA createDSAImpl(short hashAlgorithm) {
        return new ECDSASigner(new HMacDSAKCalculator(TlsUtils.createHash(hashAlgorithm)));
    }

    protected short getSignatureAlgorithm() {
        return SignatureAlgorithm.ecdsa;
    }
}
