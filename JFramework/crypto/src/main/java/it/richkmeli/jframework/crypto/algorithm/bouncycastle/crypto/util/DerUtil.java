package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.util;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.asn1.ASN1OctetString;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.asn1.ASN1Primitive;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.asn1.DEROctetString;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.util.Arrays;

import java.io.IOException;

class DerUtil {
    static ASN1OctetString getOctetString(byte[] data) {
        if (data == null) {
            return new DEROctetString(new byte[0]);
        }

        return new DEROctetString(Arrays.clone(data));
    }

    static byte[] toByteArray(ASN1Primitive primitive) {
        try {
            return primitive.getEncoded();
        } catch (final IOException e) {
            throw new IllegalStateException("Cannot get encoding: " + e.getMessage()) {
                public Throwable getCause() {
                    return e;
                }
            };
        }
    }
}
