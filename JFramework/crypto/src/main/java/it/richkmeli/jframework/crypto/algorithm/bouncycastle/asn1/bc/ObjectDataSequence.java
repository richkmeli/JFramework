package it.richkmeli.jframework.crypto.algorithm.bouncycastle.asn1.bc;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.asn1.*;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.util.Arrays;

import java.util.Iterator;

/**
 * <pre>
 * ObjectDataSequence ::= SEQUENCE OF ObjectData
 * </pre>
 */
public class ObjectDataSequence
        extends ASN1Object
        implements it.richkmeli.jframework.crypto.algorithm.bouncycastle.util.Iterable<ASN1Encodable> {
    private final ASN1Encodable[] dataSequence;

    public ObjectDataSequence(ObjectData[] dataSequence) {
        this.dataSequence = new ASN1Encodable[dataSequence.length];

        System.arraycopy(dataSequence, 0, this.dataSequence, 0, dataSequence.length);
    }

    private ObjectDataSequence(ASN1Sequence seq) {
        dataSequence = new ASN1Encodable[seq.size()];

        for (int i = 0; i != dataSequence.length; i++) {
            dataSequence[i] = ObjectData.getInstance(seq.getObjectAt(i));
        }
    }

    public static ObjectDataSequence getInstance(
            Object obj) {
        if (obj instanceof ObjectDataSequence) {
            return (ObjectDataSequence) obj;
        } else if (obj != null) {
            return new ObjectDataSequence(ASN1Sequence.getInstance(obj));
        }

        return null;
    }

    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(dataSequence);
    }

    public Iterator<ASN1Encodable> iterator() {
        return new Arrays.Iterator<ASN1Encodable>(dataSequence);
    }
}
