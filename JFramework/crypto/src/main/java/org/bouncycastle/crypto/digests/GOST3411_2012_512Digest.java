package org.bouncycastle.crypto.digests;

import org.bouncycastle.util.Memoable;

/**
 * implementation of GOST R 34.11-2012 512-bit
 */
public class GOST3411_2012_512Digest
        extends GOST3411_2012Digest {
    private final static byte[] IV = {
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
    };

    public GOST3411_2012_512Digest() {
        super(IV);
    }

    public GOST3411_2012_512Digest(GOST3411_2012_512Digest other) {
        super(IV);
        reset(other);
    }

    public String getAlgorithmName() {
        return "GOST3411-2012-512";
    }

    public int getDigestSize() {
        return 64;
    }

    public Memoable copy() {
        return new GOST3411_2012_512Digest(this);
    }
}
