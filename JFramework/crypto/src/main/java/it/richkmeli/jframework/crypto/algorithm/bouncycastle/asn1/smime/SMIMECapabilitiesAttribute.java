package it.richkmeli.jframework.crypto.algorithm.bouncycastle.asn1.smime;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.asn1.DERSequence;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.asn1.DERSet;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.asn1.cms.Attribute;

public class SMIMECapabilitiesAttribute
        extends Attribute {
    public SMIMECapabilitiesAttribute(
            SMIMECapabilityVector capabilities) {
        super(SMIMEAttributes.smimeCapabilities,
                new DERSet(new DERSequence(capabilities.toASN1EncodableVector())));
    }
}
