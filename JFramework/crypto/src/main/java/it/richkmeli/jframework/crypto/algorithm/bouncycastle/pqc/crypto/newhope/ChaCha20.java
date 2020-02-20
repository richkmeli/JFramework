package it.richkmeli.jframework.crypto.algorithm.bouncycastle.pqc.crypto.newhope;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.engines.ChaChaEngine;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.KeyParameter;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.ParametersWithIV;

class ChaCha20 {
    static void process(byte[] key, byte[] nonce, byte[] buf, int off, int len) {
        ChaChaEngine e = new ChaChaEngine(20);
        e.init(true, new ParametersWithIV(new KeyParameter(key), nonce));
        e.processBytes(buf, off, len, buf, off);
    }
}
