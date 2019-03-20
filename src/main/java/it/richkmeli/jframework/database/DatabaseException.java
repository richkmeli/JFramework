package it.richkmeli.jframework.database;

@SuppressWarnings("serial")
public class DatabaseException extends Exception {

    public DatabaseException(Exception exception) {
        super(exception);
    }

}