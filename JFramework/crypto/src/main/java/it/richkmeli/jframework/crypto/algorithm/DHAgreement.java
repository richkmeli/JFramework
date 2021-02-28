package it.richkmeli.jframework.crypto.algorithm;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;

public class DHAgreement extends org.bouncycastle.crypto.agreement.DHAgreement {
    public void setPrivateValue(AsymmetricKeyParameter privateValue) {
        this.privateValue = ((DHPrivateKeyParameters) privateValue).getX();
    }

}