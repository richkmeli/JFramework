package it.richkmeli.jframework.crypto.algorithm.bouncycastle.math.ec.endo;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.math.ec.ECPointMap;

public interface ECEndomorphism {
    ECPointMap getPointMap();

    boolean hasEfficientPointMap();
}
