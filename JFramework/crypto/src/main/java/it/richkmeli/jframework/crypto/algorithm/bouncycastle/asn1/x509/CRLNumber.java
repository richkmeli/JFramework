package it.richkmeli.jframework.crypto.algorithm.bouncycastle.asn1.x509;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.asn1.ASN1Integer;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.asn1.ASN1Object;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.asn1.ASN1Primitive;

import java.math.BigInteger;

/**
 * The CRLNumber object.
 * <pre>
 * CRLNumber::= INTEGER(0..MAX)
 * </pre>
 */
public class CRLNumber
        extends ASN1Object {
    private BigInteger number;

    public CRLNumber(
            BigInteger number) {
        this.number = number;
    }

    public BigInteger getCRLNumber() {
        return number;
    }

    public String toString() {
        return "CRLNumber: " + getCRLNumber();
    }

    public ASN1Primitive toASN1Primitive() {
        return new ASN1Integer(number);
    }

    public static CRLNumber getInstance(Object o) {
        if (o instanceof CRLNumber) {
            return (CRLNumber) o;
        } else if (o != null) {
            return new CRLNumber(ASN1Integer.getInstance(o).getValue());
        }

        return null;
    }
}
