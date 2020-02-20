package it.richkmeli.jframework.crypto.algorithm.bouncycastle.math.field;

public interface Polynomial {
    int getDegree();

//    BigInteger[] getCoefficients();

    int[] getExponentsPresent();

//    Term[] getNonZeroTerms();
}
