package it.richkmeli.jframework.util.regex.exception;

public class RegexException extends Exception {
    public RegexException(Exception exception) {
        super(exception);

    }

    public RegexException(String string) {
        super(new Exception(string));

    }
}
