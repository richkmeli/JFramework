package it.richkmeli.jframework.crypto.algorithm.bouncycastle.pqc.crypto.newhope;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params.AsymmetricKeyParameter;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.pqc.crypto.ExchangePair;
import it.richkmeli.jframework.crypto.algorithm.bouncycastle.pqc.crypto.ExchangePairGenerator;

import java.security.SecureRandom;

public class NHExchangePairGenerator
        implements ExchangePairGenerator {
    private final SecureRandom random;

    public NHExchangePairGenerator(SecureRandom random) {
        this.random = random;
    }

    public ExchangePair GenerateExchange(AsymmetricKeyParameter senderPublicKey) {
        return generateExchange(senderPublicKey);
    }

    public ExchangePair generateExchange(AsymmetricKeyParameter senderPublicKey) {
        NHPublicKeyParameters pubKey = (NHPublicKeyParameters) senderPublicKey;

        byte[] sharedValue = new byte[NewHope.AGREEMENT_SIZE];
        byte[] publicKeyValue = new byte[NewHope.SENDB_BYTES];

        NewHope.sharedB(random, sharedValue, publicKeyValue, pubKey.pubData);

        return new ExchangePair(new NHPublicKeyParameters(publicKeyValue), sharedValue);
    }
}
