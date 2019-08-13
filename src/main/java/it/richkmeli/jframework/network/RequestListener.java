package it.richkmeli.jframework.network;

// M is the generic model
public interface RequestListener<M> {
    void onResult(M response);
}
