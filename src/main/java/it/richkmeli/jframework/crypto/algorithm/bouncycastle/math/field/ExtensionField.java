package it.richkmeli.jframework.crypto.algorithm.bouncycastle.math.field;

public interface ExtensionField extends FiniteField {
    FiniteField getSubfield();

    int getDegree();
}
