package it.richkmeli.jframework.crypto.algorithm.bouncycastle.crypto.params;

import it.richkmeli.jframework.crypto.algorithm.bouncycastle.math.ec.ECPoint;

public class ECPublicKeyParameters
        extends ECKeyParameters {
    private final ECPoint q;

    public ECPublicKeyParameters(
            ECPoint q,
            ECDomainParameters parameters) {
        super(false, parameters);

        this.q = parameters.validatePublicPoint(q);
    }

    public ECPoint getQ() {
        return q;
    }
}
