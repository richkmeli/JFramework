package org.bouncycastle.crypto;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

import java.io.IOException;
import java.io.InputStream;

public interface KeyParser {
    AsymmetricKeyParameter readKey(InputStream stream)
            throws IOException;
}
