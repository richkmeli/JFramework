package it.richkmeli.jframework.crypto.controller.token;

import org.junit.Test;

public class TokenManagerTest {

    @Test
    public void generate_verify() {
        String token = TokenManager.generate("test");

        System.out.println(token);

        assert TokenManager.verify(token, "test");
    }

    @Test
    public void generate_verify_temporized() {
        String token = TokenManager.generateTemporized("test", 2);

        System.out.println(token);

        assert TokenManager.verifyTemporized(token, "test", 2);
    }

}