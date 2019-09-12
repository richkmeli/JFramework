package it.richkmeli.jframework.network.tcp.client.api.client.api;

// M is the generic model
public interface RequestListener<M> {
    void onResult(M response);
}
