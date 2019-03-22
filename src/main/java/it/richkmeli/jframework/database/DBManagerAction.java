package it.richkmeli.jframework.database;

public interface DBManagerAction<T> {
    T action(T type);
}
