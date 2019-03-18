package it.richkmeli.jframework.network;

public interface RequestListener<MODEL> {
    void onResult(MODEL response);
}
