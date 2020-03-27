package it.richkmeli.jframework.util;

public class TypeConverter {

    public static String bytesToHex(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            char[] hexDigits = new char[2];
            hexDigits[0] = Character.forDigit((b >> 4) & 0xF, 16);
            hexDigits[1] = Character.forDigit((b & 0xF), 16);
            stringBuilder.append(hexDigits);
        }
        return stringBuilder.toString();
    }

    public static byte[] hexToBytes(String string) {
        byte[] out = new byte[string.length() / 2];
        for (int i = 0, j = 0; i < string.length(); i = i + 2, j++) {
            out[j] = hexToByte(string.substring(i, i + 2));
        }
        return out;
    }

    private static byte hexToByte(String hexString) {
        int firstDigit = toDigit(hexString.charAt(0));
        int secondDigit = toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }

    private static int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if (digit == -1) {
            throw new IllegalArgumentException(
                    "Invalid Hexadecimal Character: " + hexChar);
        }
        return digit;
    }
}

