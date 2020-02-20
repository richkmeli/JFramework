package it.richkmeli.jframework.crypto.algorithm.bouncycastle.pqc.crypto.util;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.asn1.x509.AlgorithmIdentifier;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.AsymmetricKeyParameter;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.pqc.asn1.*;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.pqc.crypto.newhope.NHPublicKeyParameters;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.pqc.crypto.qtesla.QTESLAPublicKeyParameters;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.pqc.crypto.sphincs.SPHINCSPublicKeyParameters;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.pqc.crypto.xmss.XMSSMTPublicKeyParameters;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.pqc.crypto.xmss.XMSSPublicKeyParameters;

import java.io.IOException;

/**
 * Factory to create ASN.1 subject public key info objects from lightweight public keys.
 */
public class SubjectPublicKeyInfoFactory {
    private SubjectPublicKeyInfoFactory() {

    }

    /**
     * Create a SubjectPublicKeyInfo public key.
     *
     * @param publicKey the key to be encoded into the info object.
     * @return a SubjectPublicKeyInfo representing the key.
     * @throws IOException on an error encoding the key
     */
    public static SubjectPublicKeyInfo createSubjectPublicKeyInfo(AsymmetricKeyParameter publicKey)
            throws IOException {
        if (publicKey instanceof QTESLAPublicKeyParameters) {
            QTESLAPublicKeyParameters keyParams = (QTESLAPublicKeyParameters) publicKey;
            AlgorithmIdentifier algorithmIdentifier = Utils.qTeslaLookupAlgID(keyParams.getSecurityCategory());

            return new SubjectPublicKeyInfo(algorithmIdentifier, keyParams.getPublicData());
        } else if (publicKey instanceof SPHINCSPublicKeyParameters) {
            SPHINCSPublicKeyParameters params = (SPHINCSPublicKeyParameters) publicKey;

            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.sphincs256,
                    new SPHINCS256KeyParams(Utils.sphincs256LookupTreeAlgID(params.getTreeDigest())));
            return new SubjectPublicKeyInfo(algorithmIdentifier, params.getKeyData());
        } else if (publicKey instanceof NHPublicKeyParameters) {
            NHPublicKeyParameters params = (NHPublicKeyParameters) publicKey;

            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.newHope);
            return new SubjectPublicKeyInfo(algorithmIdentifier, params.getPubData());
        } else if (publicKey instanceof XMSSPublicKeyParameters) {
            XMSSPublicKeyParameters keyParams = (XMSSPublicKeyParameters) publicKey;

            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.xmss,
                    new XMSSKeyParams(keyParams.getParameters().getHeight(), Utils.xmssLookupTreeAlgID(keyParams.getTreeDigest())));
            return new SubjectPublicKeyInfo(algorithmIdentifier, new XMSSPublicKey(keyParams.getPublicSeed(), keyParams.getRoot()));
        } else if (publicKey instanceof XMSSMTPublicKeyParameters) {
            XMSSMTPublicKeyParameters keyParams = (XMSSMTPublicKeyParameters) publicKey;

            AlgorithmIdentifier algorithmIdentifier = new AlgorithmIdentifier(PQCObjectIdentifiers.xmss_mt, new XMSSMTKeyParams(keyParams.getParameters().getHeight(), keyParams.getParameters().getLayers(),
                    Utils.xmssLookupTreeAlgID(keyParams.getTreeDigest())));
            return new SubjectPublicKeyInfo(algorithmIdentifier, new XMSSMTPublicKey(keyParams.getPublicSeed(), keyParams.getRoot()));
        } else {
            throw new IOException("key parameters not recognized");
        }
    }
}
