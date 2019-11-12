package it.richkmeli.jframework.crypto.algorithm.bouncycastle.asn1.crmf;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.asn1.*;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.asn1.x509.GeneralName;

public class EncKeyWithID
        extends ASN1Object {
    private final PrivateKeyInfo privKeyInfo;
    private final ASN1Encodable identifier;

    public static EncKeyWithID getInstance(Object o) {
        if (o instanceof EncKeyWithID) {
            return (EncKeyWithID) o;
        } else if (o != null) {
            return new EncKeyWithID(ASN1Sequence.getInstance(o));
        }

        return null;
    }

    private EncKeyWithID(ASN1Sequence seq) {
        this.privKeyInfo = PrivateKeyInfo.getInstance(seq.getObjectAt(0));

        if (seq.size() > 1) {
            if (!(seq.getObjectAt(1) instanceof DERUTF8String)) {
                this.identifier = GeneralName.getInstance(seq.getObjectAt(1));
            } else {
                this.identifier = (ASN1Encodable) seq.getObjectAt(1);
            }
        } else {
            this.identifier = null;
        }
    }

    public EncKeyWithID(PrivateKeyInfo privKeyInfo) {
        this.privKeyInfo = privKeyInfo;
        this.identifier = null;
    }

    public EncKeyWithID(PrivateKeyInfo privKeyInfo, DERUTF8String str) {
        this.privKeyInfo = privKeyInfo;
        this.identifier = str;
    }

    public EncKeyWithID(PrivateKeyInfo privKeyInfo, GeneralName generalName) {
        this.privKeyInfo = privKeyInfo;
        this.identifier = generalName;
    }

    public PrivateKeyInfo getPrivateKey() {
        return privKeyInfo;
    }

    public boolean hasIdentifier() {
        return identifier != null;
    }

    public boolean isIdentifierUTF8String() {
        return identifier instanceof DERUTF8String;
    }

    public ASN1Encodable getIdentifier() {
        return identifier;
    }

    /**
     * <pre>
     * EncKeyWithID ::= SEQUENCE {
     *      privateKey           PrivateKeyInfo,
     *      identifier CHOICE {
     *         string               UTF8String,
     *         generalName          GeneralName
     *     } OPTIONAL
     * }
     * </pre>
     *
     * @return an ASN.1 primitive composition of this EncKeyWithID.
     */
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);

        v.add(privKeyInfo);

        if (identifier != null) {
            v.add(identifier);
        }

        return new DERSequence(v);
    }
}
