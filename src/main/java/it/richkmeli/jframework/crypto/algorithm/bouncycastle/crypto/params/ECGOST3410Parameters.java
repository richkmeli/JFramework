package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.asn1.ASN1ObjectIdentifier;

public class ECGOST3410Parameters
        extends ECNamedDomainParameters {
    private final ASN1ObjectIdentifier publicKeyParamSet;
    private final ASN1ObjectIdentifier digestParamSet;
    private final ASN1ObjectIdentifier encryptionParamSet;

    public ECGOST3410Parameters(ECDomainParameters ecParameters, ASN1ObjectIdentifier publicKeyParamSet, ASN1ObjectIdentifier digestParamSet) {
        this(ecParameters, publicKeyParamSet, digestParamSet, null);
    }

    public ECGOST3410Parameters(ECDomainParameters ecParameters, ASN1ObjectIdentifier publicKeyParamSet, ASN1ObjectIdentifier digestParamSet, ASN1ObjectIdentifier encryptionParamSet) {
        super(publicKeyParamSet, ecParameters.getCurve(), ecParameters.getG(), ecParameters.getN(), ecParameters.getH(), ecParameters.getSeed());

        if (ecParameters instanceof ECNamedDomainParameters) {
            if (!publicKeyParamSet.equals(((ECNamedDomainParameters) ecParameters).getName())) {
                throw new IllegalArgumentException("named parameters do not match publicKeyParamSet value");
            }
        }
        this.publicKeyParamSet = publicKeyParamSet;
        this.digestParamSet = digestParamSet;
        this.encryptionParamSet = encryptionParamSet;
    }

    public ASN1ObjectIdentifier getPublicKeyParamSet() {
        return publicKeyParamSet;
    }

    public ASN1ObjectIdentifier getDigestParamSet() {
        return digestParamSet;
    }

    public ASN1ObjectIdentifier getEncryptionParamSet() {
        return encryptionParamSet;
    }
}
