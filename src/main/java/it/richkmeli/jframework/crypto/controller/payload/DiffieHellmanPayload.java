package it.richkmeli.jframework.crypto.controller.payload;

import java.math.BigInteger;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class DiffieHellmanPayload {
    private List<BigInteger> pg;
    private PublicKey publicKey;
    private BigInteger message;

    public DiffieHellmanPayload(List<BigInteger> list, PublicKey publicKey) {
        pg = new ArrayList<>();
        pg.addAll(list);
        this.publicKey = publicKey;
    }

    public List<BigInteger> getPG() {
        return pg;

    }

    public PublicKey getPublicKey() {
        return publicKey;
    }


}
