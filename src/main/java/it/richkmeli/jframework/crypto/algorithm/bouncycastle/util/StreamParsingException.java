package it.richkmeli.jframework.crypto.algorithm.bouncycastle.util;

public class StreamParsingException
        extends Exception {
    Throwable _e;

    public StreamParsingException(String message, Throwable e) {
        super(message);
        _e = e;
    }

    public Throwable getCause() {
        return _e;
    }
}
