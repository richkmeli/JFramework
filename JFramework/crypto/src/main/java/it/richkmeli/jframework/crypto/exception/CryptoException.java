package it.richkmeli.jframework.crypto.exception;

public class CryptoException extends Exception {
    public CryptoException(Exception exception) {
        super(exception);
    }

    public CryptoException(String string) {
        super(new Exception(string));
    }
}