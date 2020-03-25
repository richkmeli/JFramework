package it.richkmeli.jframework.auth.model.exception;

public class ModelException extends Exception {
    public ModelException(Exception exception) {
        super(exception);

    }

    public ModelException(String string) {
        super(new Exception(string));

    }
}
