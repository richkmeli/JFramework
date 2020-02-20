package it.richkmeli.jframework.crypto.algorithm;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.AsymmetricKeyParameter;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.DHPrivateKeyParameters;

public class DHAgreement extends it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.agreement.DHAgreement {
    public void setPrivateValue(AsymmetricKeyParameter privateValue) {
        this.privateValue = ((DHPrivateKeyParameters) privateValue).getX();
    }

}