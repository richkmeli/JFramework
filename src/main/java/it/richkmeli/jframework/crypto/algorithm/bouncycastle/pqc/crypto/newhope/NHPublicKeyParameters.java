package it.richkmeli.jframework.crypto.algorithm.bouncycastle.pqc.crypto.newhope;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.AsymmetricKeyParameter;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.util.Arrays;

public class NHPublicKeyParameters
        extends AsymmetricKeyParameter {
    final byte[] pubData;

    public NHPublicKeyParameters(byte[] pubData) {
        super(false);
        this.pubData = Arrays.clone(pubData);
    }

    /**
     * Return the public key data.
     *
     * @return the public key values.
     */
    public byte[] getPubData() {
        return Arrays.clone(pubData);
    }
}
