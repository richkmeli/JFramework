package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.asn1.ASN1ObjectIdentifier;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.math.ec.ECConstants;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.math.ec.ECCurve;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;

public class ECNamedDomainParameters
        extends ECDomainParameters {
    private ASN1ObjectIdentifier name;

    public ECNamedDomainParameters(ASN1ObjectIdentifier name, ECCurve curve, ECPoint G, BigInteger n) {
        this(name, curve, G, n, ECConstants.ONE, null);
    }

    public ECNamedDomainParameters(ASN1ObjectIdentifier name, ECCurve curve, ECPoint G, BigInteger n, BigInteger h) {
        this(name, curve, G, n, h, null);
    }

    public ECNamedDomainParameters(ASN1ObjectIdentifier name, ECCurve curve, ECPoint G, BigInteger n, BigInteger h, byte[] seed) {
        super(curve, G, n, h, seed);

        this.name = name;
    }

    public ECNamedDomainParameters(ASN1ObjectIdentifier name, ECDomainParameters domainParameters) {
        super(domainParameters.getCurve(), domainParameters.getG(), domainParameters.getN(), domainParameters.getH(), domainParameters.getSeed());
        this.name = name;
    }

    public ASN1ObjectIdentifier getName() {
        return name;
    }
}
