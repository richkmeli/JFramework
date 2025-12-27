package verification;

import it.richkmeli.jframework.crypto.algorithm.AES;

public class Verification {
    public static void main(String[] args) {
        String key = "mysecretkey";
        try {
            // Debug Key Hash
            // Reflection or modify AES to public? AES.generateKey is private.
            // But we can reproduce it.
            java.security.MessageDigest sha = java.security.MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = key.getBytes(java.nio.charset.StandardCharsets.UTF_8);
            keyBytes = sha.digest(keyBytes);
            System.out.print("KeyHash: ");
            for (byte b : keyBytes)
                System.out.printf("%02x", b);
            System.out.println();

            String encrypted = args[0];
            String decrypted = AES.decrypt(encrypted, key);
            System.out.println("Decrypted: " + decrypted);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
