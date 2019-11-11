package it.richkmeli.jframework.crypto.algorithm.bc;

import java.math.BigInteger;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class DiffieHellmanPayload_BC {
    private List<BigInteger> pg;
    private PublicKey a;

    public DiffieHellmanPayload_BC(List<BigInteger> list, PublicKey a) {
        pg = new ArrayList<>();
        pg.addAll(list);
        this.a = a;
    }

    public List<BigInteger> getPG() {
        return pg;

    }

    public PublicKey getA() {
        return a;
    }

}
