package crypto.algorithm;

public class RSATest {
//    @Test
//    public void rsaEncryptdecrypt() {
//        try {
//            KeyPair keyPair = RSA.generateKeyPair();
//
//            PublicKey RSApublicKeyClient = keyPair.getPublic();
//            PrivateKey RSAprivateKeyClient = keyPair.getPrivate();
//
//            int[] plainTextLenghts = {8, 10, 100, 245};
//
//            for (int i : plainTextLenghts) {
//
//                byte[] plain = genString(i).getBytes();
//
//                byte[] decrypted = null;
//                try {
//                    byte[] encrypted = RSA.encrypt(plain, RSApublicKeyClient);
//
//                    decrypted = RSA.decrypt(encrypted, RSAprivateKeyClient);
//
//                } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchProviderException e) {
//                    e.printStackTrace();
//                }
//
//                assertEquals(Arrays.toString(plain), Arrays.toString(decrypted));
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            assert false;
//        }
//    }
//
//    @Test
//    public void rsaKey() {
//        try {
//            KeyPair keyPair = RSA.generateKeyPair();
//
//            String priv = RSA.savePrivateKey(keyPair.getPrivate());
//            String pub = RSA.savePublicKey(keyPair.getPublic());
//
//            PrivateKey privateKey = RSA.loadPrivateKey(priv);
//            PublicKey publicKey = RSA.loadPublicKey(pub);
//
//            assertEquals(keyPair.getPublic(), publicKey);
//            assertEquals(keyPair.getPrivate(), privateKey);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            assert false;
//        }
//    }

}