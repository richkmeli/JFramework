package it.richkmeli.jframework.network.tcp.client.okhttp;

public interface NetworkCallback {

    void onSuccess(String response);

    void onFailure(Exception e);
}
