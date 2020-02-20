package it.richkmeli.jframework.crypto.algorithm.bouncycastle.math.ec;

public interface PreCompCallback {
    PreCompInfo precompute(PreCompInfo existing);
}
