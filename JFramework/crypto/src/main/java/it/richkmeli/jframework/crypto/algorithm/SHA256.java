package it.richkmeli.jframework.crypto.algorithm;

import org.bouncycastle.crypto.digests.SHA256Digest;
import it.richkmeli.jframework.util.TypeConverter;

public class SHA256 {
    public static byte[] hash(byte[] input) {
        SHA256Digest digest = new SHA256Digest();
        byte[] output = new byte[32];
        digest.update(input, 0, input.length);
        digest.doFinal(output, 0);
        return output;
    }


    // sha256: string to hex
    public static String hash(String input) {
        return TypeConverter.bytesToHex(hash(input.getBytes()));
    }

    public static String hashToString(byte[] input) {
        return TypeConverter.bytesToHex(hash(input));
    }
}
