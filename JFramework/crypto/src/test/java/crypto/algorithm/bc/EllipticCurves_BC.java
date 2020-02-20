package crypto.algorithm.bc;

public class EllipticCurves_BC {
    public static final String ALGORITHM = "ECDH"; //
    public static final String STD_NAME = "brainpoolp256r1"; //sect571r1 : NIST/SECG curve over a 571 bit binary field
    public static final String CIPHER_ALGO = "AES/GCM/NoPadding"; // ECIESwithAES
    private static final String PROVIDER = "BC";

//    public static KeyPair generateKP() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
//        KeyPairGenerator kpgen = KeyPairGenerator.getInstance(ALGORITHM, PROVIDER);
//        kpgen.initialize(new ECGenParameterSpec(STD_NAME), new SecureRandom());
//        return kpgen.generateKeyPair();
//    }
//
//    public static SecretKey generateSharedSecret(PrivateKey privateKey, PublicKey publicKey) {
//        try {
//            KeyAgreement keyAgreement = KeyAgreement.getInstance(ALGORITHM, PROVIDER);
//            keyAgreement.init(privateKey);
//            keyAgreement.doPhase(publicKey, true);
//
//            SecretKey key = keyAgreement.generateSecret("AES");
//            return key;
//        } catch (InvalidKeyException | NoSuchAlgorithmException
//                | NoSuchProviderException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    public static String encrypt(String plaintext, byte[] key) throws Exception {
//        byte[] plaintextbyte = plaintext.getBytes(Charset.defaultCharset());
//        KeyPair pairClient = it.richkmeli.jframework.crypto.algorithm.EllipticCurves.generateKP();
//        SecretKey s = it.richkmeli.jframework.crypto.algorithm.EllipticCurves.generateSharedSecret(pairClient.getPrivate(), loadPublicKey(key));
//        Cipher cipher = Cipher.getInstance(CIPHER_ALGO, PROVIDER);
//        cipher.init(Cipher.ENCRYPT_MODE, s);
//        byte[] encryptedText = cipher.doFinal(plaintextbyte);
//        return Base64.getEncoder().encodeToString(encryptedText) + "##" + Base64.getEncoder().encodeToString(savePublicKey(pairClient.getPublic()));
//
//    }
//
//    public static String decrypt(String ciphertext, byte[] key) throws Exception {
//        String[] data = ciphertext.split("##");
//        String encData = data[0];
//        String kpubClient = data[1];
//        SecretKey s = generateSharedSecret(loadPrivateKey(key), loadPublicKey(kpubClient.getBytes(Charset.defaultCharset())));
//
//        Cipher cipher = Cipher.getInstance(CIPHER_ALGO, PROVIDER);
//        cipher.init(Cipher.DECRYPT_MODE, s);
//        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
//        return new String(decrypted);
//    }


//    public static byte[] savePublicKey(PublicKey key) throws Exception {
//        //return key.getEncoded();
//
//        ECPublicKey eckey = (ECPublicKey) key;
//        return eckey.getQ().getEncoded(true);
//    }
//
//    public static PublicKey loadPublicKey(byte[] data) throws Exception {
//		/*KeyFactory kf = KeyFactory.getInstance("ECDH", "BC");
//		return kf.generatePublic(new X509EncodedKeySpec(data));*/
//
//        ECParameterSpec params = ECNamedCurveTable.getParameterSpec(STD_NAME);
//        ECPublicKeySpec pubKey = new ECPublicKeySpec(
//                params.getCurve().decodePoint(data), params);
//        KeyFactory kf = KeyFactory.getInstance(ALGORITHM, PROVIDER);
//        return kf.generatePublic(pubKey);
//    }
//
//    public static byte[] savePrivateKey(PrivateKey key) throws Exception {
//        //return key.getEncoded();
//        ECPrivateKey eckey = (ECPrivateKey) key;
//        return eckey.getD().toByteArray();
//    }
//
//    public static PrivateKey loadPrivateKey(byte[] data) throws Exception {
//        //KeyFactory kf = KeyFactory.getInstance("ECDH", "BC");
//        //return kf.generatePrivate(new PKCS8EncodedKeySpec(data));
//
//        ECParameterSpec params = ECNamedCurveTable.getParameterSpec(STD_NAME);
//        ECPrivateKeySpec prvkey = new ECPrivateKeySpec(new BigInteger(data), params);
//        KeyFactory kf = KeyFactory.getInstance(ALGORITHM, PROVIDER);
//        return kf.generatePrivate(prvkey);
//    }
}
