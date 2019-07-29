package it.richkmeli.jframework.crypto.util;

public interface CryptoListener {
    void onResult(String result);

    void onError(String result);
}
