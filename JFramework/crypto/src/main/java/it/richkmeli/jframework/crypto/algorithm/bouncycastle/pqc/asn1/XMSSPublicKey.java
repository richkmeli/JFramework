package it.richkmeli.jframework.crypto.algorithm.bouncycastle.pqc.asn1;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.asn1.*;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.util.Arrays;

import java.math.BigInteger;

/**
 * XMSSPublicKey
 * <pre>
 *     XMSSPublicKey ::= SEQUENCE {
 *         version       INTEGER -- 0
 *         publicSeed    OCTET STRING
 *         root          OCTET STRING
 *    }
 * </pre>
 */
public class XMSSPublicKey
        extends ASN1Object {
    private final byte[] publicSeed;
    private final byte[] root;

    public XMSSPublicKey(byte[] publicSeed, byte[] root) {
        this.publicSeed = Arrays.clone(publicSeed);
        this.root = Arrays.clone(root);
    }

    private XMSSPublicKey(ASN1Sequence seq) {
        if (!ASN1Integer.getInstance(seq.getObjectAt(0)).hasValue(BigInteger.valueOf(0))) {
            throw new IllegalArgumentException("unknown version of sequence");
        }

        this.publicSeed = Arrays.clone(DEROctetString.getInstance(seq.getObjectAt(1)).getOctets());
        this.root = Arrays.clone(DEROctetString.getInstance(seq.getObjectAt(2)).getOctets());
    }

    public static XMSSPublicKey getInstance(Object o) {
        if (o instanceof XMSSPublicKey) {
            return (XMSSPublicKey) o;
        } else if (o != null) {
            return new XMSSPublicKey(ASN1Sequence.getInstance(o));
        }

        return null;
    }

    public byte[] getPublicSeed() {
        return Arrays.clone(publicSeed);
    }

    public byte[] getRoot() {
        return Arrays.clone(root);
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector();

        v.add(new ASN1Integer(0)); // version

        v.add(new DEROctetString(publicSeed));
        v.add(new DEROctetString(root));

        return new DERSequence(v);
    }
}
