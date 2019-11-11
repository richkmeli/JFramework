package it.richkmeli.jframework.crypto.algorithm.bouncycastle.math.field;

import java.math.BigInteger;

public interface FiniteField {
    BigInteger getCharacteristic();

    int getDimension();
}
