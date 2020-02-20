package it.richkmeli.jframework.crypto.algorithm.bouncycastle.asn1.cmc;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.asn1.*;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.asn1.crmf.PKIPublicationInfo;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.asn1.x509.AlgorithmIdentifier;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.util.Arrays;

/**
 * <pre>
 *      CMCPublicationInfo ::= SEQUENCE {
 *           hashAlg                      AlgorithmIdentifier,
 *           certHashes                   SEQUENCE OF OCTET STRING,
 *           pubInfo                      PKIPublicationInfo
 * }
 *
 * </pre>
 */
public class CMCPublicationInfo
        extends ASN1Object {
    private final AlgorithmIdentifier hashAlg;
    private final ASN1Sequence certHashes;
    private final PKIPublicationInfo pubInfo;

    public CMCPublicationInfo(AlgorithmIdentifier hashAlg, byte[][] anchorHashes, PKIPublicationInfo pubInfo) {
        this.hashAlg = hashAlg;

        ASN1EncodableVector v = new ASN1EncodableVector(anchorHashes.length);
        for (int i = 0; i != anchorHashes.length; i++) {
            v.add(new DEROctetString(Arrays.clone(anchorHashes[i])));
        }
        this.certHashes = new DERSequence(v);

        this.pubInfo = pubInfo;
    }

    private CMCPublicationInfo(ASN1Sequence seq) {
        if (seq.size() != 3) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.hashAlg = AlgorithmIdentifier.getInstance(seq.getObjectAt(0));
        this.certHashes = ASN1Sequence.getInstance(seq.getObjectAt(1));
        this.pubInfo = PKIPublicationInfo.getInstance(seq.getObjectAt(2));
    }

    public static CMCPublicationInfo getInstance(Object o) {
        if (o instanceof CMCPublicationInfo) {
            return (CMCPublicationInfo) o;
        }

        if (o != null) {
            return new CMCPublicationInfo(ASN1Sequence.getInstance(o));
        }

        return null;
    }

    public AlgorithmIdentifier getHashAlg() {
        return hashAlg;
    }

    public byte[][] getCertHashes() {
        byte[][] hashes = new byte[certHashes.size()][];

        for (int i = 0; i != hashes.length; i++) {
            hashes[i] = Arrays.clone(ASN1OctetString.getInstance(certHashes.getObjectAt(i)).getOctets());
        }

        return hashes;
    }

    public PKIPublicationInfo getPubInfo() {
        return pubInfo;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(3);

        v.add(hashAlg);
        v.add(certHashes);
        v.add(pubInfo);

        return new DERSequence(v);
    }
}
