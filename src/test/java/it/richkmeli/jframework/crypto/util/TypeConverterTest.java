package it.richkmeli.jframework.crypto.util;

import org.junit.Test;

import static it.richkmeli.jframework.crypto.algorithm.algorithmTestUtil.genString;
import static it.richkmeli.jframework.crypto.algorithm.algorithmTestUtil.plainTextLengths;
import static org.junit.Assert.assertEquals;

public class TypeConverterTest {
    private static int[] lengths = {8, 10, 13, 17, 53, 100, 1000};

    @Test
    public void bytes_hex() {
        for (int i : plainTextLengths) {
            String plain = genString(i);

            String hex = TypeConverter.bytesToHex(plain.getBytes());
            String bytes = new String(TypeConverter.hexToBytes(hex));

            assertEquals(plain, bytes);
        }
    }
}