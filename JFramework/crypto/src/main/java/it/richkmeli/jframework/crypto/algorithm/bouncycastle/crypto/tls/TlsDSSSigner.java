package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.tls;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.DSA;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.AsymmetricKeyParameter;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.DSAPublicKeyParameters;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.signers.DSASigner;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.signers.HMacDSAKCalculator;

/**
 * @deprecated Migrate to the (D)TLS API in it.richkmeli.jframework.crypto.algorithm.bouncycastle.tls (bctls jar).
 */
public class TlsDSSSigner
        extends TlsDSASigner {
    public boolean isValidPublicKey(AsymmetricKeyParameter publicKey) {
        return publicKey instanceof DSAPublicKeyParameters;
    }

    protected DSA createDSAImpl(short hashAlgorithm) {
        return new DSASigner(new HMacDSAKCalculator(TlsUtils.createHash(hashAlgorithm)));
    }

    protected short getSignatureAlgorithm() {
        return SignatureAlgorithm.dsa;
    }
}
