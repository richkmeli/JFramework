package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.tls;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.CryptoException;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.Signer;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.AsymmetricKeyParameter;

/**
 * @deprecated Migrate to the (D)TLS API in it.richkmeli.jframework.crypto.algorithm.bouncycastle.tls (bctls jar).
 */
public abstract class AbstractTlsSigner
        implements TlsSigner {
    protected TlsContext context;

    public void init(TlsContext context) {
        this.context = context;
    }

    public byte[] generateRawSignature(AsymmetricKeyParameter privateKey, byte[] md5AndSha1)
            throws CryptoException {
        return generateRawSignature(null, privateKey, md5AndSha1);
    }

    public boolean verifyRawSignature(byte[] sigBytes, AsymmetricKeyParameter publicKey, byte[] md5AndSha1)
            throws CryptoException {
        return verifyRawSignature(null, sigBytes, publicKey, md5AndSha1);
    }

    public Signer createSigner(AsymmetricKeyParameter privateKey) {
        return createSigner(null, privateKey);
    }

    public Signer createVerifyer(AsymmetricKeyParameter publicKey) {
        return createVerifyer(null, publicKey);
    }
}
