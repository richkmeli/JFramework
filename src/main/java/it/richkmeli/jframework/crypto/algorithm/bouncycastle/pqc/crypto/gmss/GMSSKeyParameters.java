package it.richkmeli.jframework.crypto.algorithm.bouncycastle.pqc.crypto.gmss;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class GMSSKeyParameters
        extends AsymmetricKeyParameter {
    private GMSSParameters params;

    public GMSSKeyParameters(
            boolean isPrivate,
            GMSSParameters params) {
        super(isPrivate);
        this.params = params;
    }

    public GMSSParameters getParameters() {
        return params;
    }
}