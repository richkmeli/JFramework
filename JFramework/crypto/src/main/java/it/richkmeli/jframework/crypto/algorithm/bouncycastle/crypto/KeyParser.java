package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.AsymmetricKeyParameter;

import java.io.IOException;
import java.io.InputStream;

public interface KeyParser {
    AsymmetricKeyParameter readKey(InputStream stream)
            throws IOException;
}
