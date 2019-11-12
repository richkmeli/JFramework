package it.richkmeli.jframework.crypto.algorithm.bouncycastle.asn1.cms;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.asn1.*;

/**
 * <a href="http://tools.ietf.org/html/rfc5544">RFC 5544</a>:
 * Binding Documents with Time-Stamps; TimeStampedData object.
 * <p>
 * <pre>
 * TimeStampedData ::= SEQUENCE {
 *   version              INTEGER { v1(1) },
 *   dataUri              IA5String OPTIONAL,
 *   metaData             MetaData OPTIONAL,
 *   content              OCTET STRING OPTIONAL,
 *   temporalEvidence     Evidence
 * }
 * </pre>
 */
public class TimeStampedData
        extends ASN1Object {
    private ASN1Integer version;
    private DERIA5String dataUri;
    private MetaData metaData;
    private ASN1OctetString content;
    private Evidence temporalEvidence;

    public TimeStampedData(DERIA5String dataUri, MetaData metaData, ASN1OctetString content, Evidence temporalEvidence) {
        this.version = new ASN1Integer(1);
        this.dataUri = dataUri;
        this.metaData = metaData;
        this.content = content;
        this.temporalEvidence = temporalEvidence;
    }

    private TimeStampedData(ASN1Sequence seq) {
        this.version = ASN1Integer.getInstance(seq.getObjectAt(0));

        int index = 1;
        if (seq.getObjectAt(index) instanceof DERIA5String) {
            this.dataUri = DERIA5String.getInstance(seq.getObjectAt(index++));
        }
        if (seq.getObjectAt(index) instanceof MetaData || seq.getObjectAt(index) instanceof ASN1Sequence) {
            this.metaData = MetaData.getInstance(seq.getObjectAt(index++));
        }
        if (seq.getObjectAt(index) instanceof ASN1OctetString) {
            this.content = ASN1OctetString.getInstance(seq.getObjectAt(index++));
        }
        this.temporalEvidence = Evidence.getInstance(seq.getObjectAt(index));
    }

    /**
     * Return a TimeStampedData object from the given object.
     * <p>
     * Accepted inputs:
     * <ul>
     * <li> null &rarr; null
     * <li> {@link RecipientKeyIdentifier} object
     * <li> {@link it.richkmeli.jframework.crypto.algorithm.bouncycastle.asn1.ASN1Sequence#getInstance(Object) ASN1Sequence} input formats with TimeStampedData structure inside
     * </ul>
     *
     * @param obj the object we want converted.
     * @throws IllegalArgumentException if the object cannot be converted.
     */
    public static TimeStampedData getInstance(Object obj) {
        if (obj == null || obj instanceof TimeStampedData) {
            return (TimeStampedData) obj;
        }
        return new TimeStampedData(ASN1Sequence.getInstance(obj));
    }

    public DERIA5String getDataUri() {
        return dataUri;
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public ASN1OctetString getContent() {
        return content;
    }

    public Evidence getTemporalEvidence() {
        return temporalEvidence;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(5);

        v.add(version);

        if (dataUri != null) {
            v.add(dataUri);
        }

        if (metaData != null) {
            v.add(metaData);
        }

        if (content != null) {
            v.add(content);
        }

        v.add(temporalEvidence);

        return new BERSequence(v);
    }
}
