package it.richkmeli.jframework.auth.data.exception;

public class AuthDatabaseException extends Exception {

    public AuthDatabaseException(Exception exception) {
        super(exception);
    }

    public AuthDatabaseException(String s, Exception exception) {
        super(s, exception);
    }

    public AuthDatabaseException(String exception) {
        super(exception);
    }

}
