package it.richkmeli.jframework.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class TypeConverterTest {
    private static int[] lengths = {8, 10, 13, 17, 53, 100, 1000};

    @Test
    public void bytes_hex() {
        for (int i : lengths) {
            String plain = RandomStringGenerator.generateAlphanumericString(i);

            String hex = TypeConverter.bytesToHex(plain.getBytes());
            String bytes = new String(TypeConverter.hexToBytes(hex));

            assertEquals(plain, bytes);
        }
    }
}