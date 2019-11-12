package it.richkmeli.jframework.crypto.algorithm.bouncycastle.pqc.crypto.mceliece;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.AsymmetricKeyParameter;


public class McElieceKeyParameters
        extends AsymmetricKeyParameter {
    private McElieceParameters params;

    public McElieceKeyParameters(
            boolean isPrivate,
            McElieceParameters params) {
        super(isPrivate);
        this.params = params;
    }


    public McElieceParameters getParameters() {
        return params;
    }

}
