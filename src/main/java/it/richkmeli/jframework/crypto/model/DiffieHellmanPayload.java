package it.richkmeli.jframework.crypto.model;

import java.math.BigInteger;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class DiffieHellmanPayload {
    private List<BigInteger> pg;
    private PublicKey a;

    public DiffieHellmanPayload(List<BigInteger> list, PublicKey a) {
        pg = new ArrayList<>();
        pg.addAll(list);
        this.a = a;
    }

    public List<BigInteger> getPQ() {
        return pg;

    }

    public PublicKey getA() {
        return a;
    }
}
