package it.richkmeli.jframework.crypto.algorithm.bouncycastle.pqc.crypto.sphincs;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.util.Arrays;

public class SPHINCSPublicKeyParameters
        extends SPHINCSKeyParameters {
    private final byte[] keyData;

    public SPHINCSPublicKeyParameters(byte[] keyData) {
        super(false, null);
        this.keyData = Arrays.clone(keyData);
    }

    public SPHINCSPublicKeyParameters(byte[] keyData, String treeDigest) {

        super(false, treeDigest);
        this.keyData = Arrays.clone(keyData);
    }

    public byte[] getKeyData() {
        return Arrays.clone(keyData);
    }
}
