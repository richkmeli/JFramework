package it.richkmeli.jframework.crypto.algorithm.bouncycastle.pqc.crypto.xmss;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class XMSSMTKeyParameters
        extends AsymmetricKeyParameter {
    private final String treeDigest;

    public XMSSMTKeyParameters(boolean isPrivateKey, String treeDigest) {
        super(isPrivateKey);
        this.treeDigest = treeDigest;
    }

    public String getTreeDigest() {
        return treeDigest;
    }
}
