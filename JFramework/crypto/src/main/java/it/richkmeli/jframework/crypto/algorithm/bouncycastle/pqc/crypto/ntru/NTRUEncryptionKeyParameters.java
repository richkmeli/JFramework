package it.richkmeli.jframework.crypto.algorithm.bouncycastle.pqc.crypto.ntru;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class NTRUEncryptionKeyParameters
        extends AsymmetricKeyParameter {
    final protected NTRUEncryptionParameters params;

    public NTRUEncryptionKeyParameters(boolean privateKey, NTRUEncryptionParameters params) {
        super(privateKey);
        this.params = params;
    }

    public NTRUEncryptionParameters getParameters() {
        return params;
    }
}
