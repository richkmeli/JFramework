package it.richkmeli.jframework.crypto.algorithm.bouncycastle.pqc.crypto.newhope;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.AsymmetricKeyParameter;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.util.Arrays;

public class NHPrivateKeyParameters
        extends AsymmetricKeyParameter {
    final short[] secData;

    public NHPrivateKeyParameters(short[] secData) {
        super(true);

        this.secData = Arrays.clone(secData);
    }

    public short[] getSecData() {
        return Arrays.clone(secData);
    }
}
