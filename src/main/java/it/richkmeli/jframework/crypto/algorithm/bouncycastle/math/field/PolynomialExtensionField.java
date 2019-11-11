package it.richkmeli.jframework.crypto.algorithm.bouncycastle.math.field;

public interface PolynomialExtensionField extends ExtensionField {
    Polynomial getMinimalPolynomial();
}
