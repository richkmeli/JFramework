package it.richkmeli.jframework.orm;

@SuppressWarnings("serial")
public class DatabaseException extends Exception {

    public DatabaseException(Exception exception) {
        super(exception);
    }

    public DatabaseException(String s, Exception exception) {
        super(s, exception);
    }

    public DatabaseException(String exception) {
        super(exception);
    }

}