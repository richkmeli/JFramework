package it.richkmeli.jframework.orm;

public interface DBManagerAction<T> {
    T action(T type);
}
