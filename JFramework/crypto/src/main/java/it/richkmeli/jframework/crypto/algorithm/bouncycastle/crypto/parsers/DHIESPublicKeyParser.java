package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.parsers;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.KeyParser;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.AsymmetricKeyParameter;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.DHParameters;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.DHPublicKeyParameters;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.util.io.Streams;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

public class DHIESPublicKeyParser
        implements KeyParser {
    private DHParameters dhParams;

    public DHIESPublicKeyParser(DHParameters dhParams) {
        this.dhParams = dhParams;
    }

    public AsymmetricKeyParameter readKey(InputStream stream)
            throws IOException {
        byte[] V = new byte[(dhParams.getP().bitLength() + 7) / 8];

        Streams.readFully(stream, V, 0, V.length);

        return new DHPublicKeyParameters(new BigInteger(1, V), dhParams);
    }
}
