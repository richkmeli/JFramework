package it.richkmeli.jframework.util;

import it.richkmeli.jframework.util.regex.RegexManager;
import it.richkmeli.jframework.util.regex.exception.RegexException;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RandomStringGeneratorTest {
    private static int[] lengths = {8, 10, 100, 1000};

    @Test
    public void generateAlphanumericString() {
        String s = "";
        for (int length : lengths) {
            s = RandomStringGenerator.generateAlphanumericString(length);
            assertEquals(length, s.length());
        }

        try {
            RegexManager.checkAlphanumericStringIntegrity(s);
            assert true;
        } catch (RegexException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    public void generateNumericString() {
        String s = "";
        for (int length : lengths) {
            s = RandomStringGenerator.generateNumericString(length);
            assertEquals(length, s.length());
        }

        try {
            RegexManager.checkNumericStringIntegrity(s);
            assert true;
        } catch (RegexException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    public void generateBoundedString() {
        String s = "";
        for (int length : lengths) {
            s = RandomStringGenerator.generateBoundedString(length, 97, 122);
            assertEquals(length, s.length());
        }

        try {
            RegexManager.validate(s, "^[a-z]*$");
            assert true;
        } catch (RegexException e) {
            e.printStackTrace();
            assert false;
        }

    }

    @Test
    public void generateUTF8string() {
        String s = "";
        for (int length : lengths) {
            s = RandomStringGenerator.generateUtf8String(length);
            //assertEquals(length, s.length());
        }

        assert DataFormat.isUTF8(s);
    }

    @Test
    public void generateUTF16string() {
        String s = "";
        for (int length : lengths) {
            s = RandomStringGenerator.generateUtf16String(length);
            //assertEquals(length, s.length());
        }

        assert DataFormat.isUTF16(s);
    }

    @Ignore
    public void generateASCIIString() {
        String s = "";
        for (int length : lengths) {
            s = RandomStringGenerator.generateASCIItring(length);
            assertEquals(length, s.length());
        }

        assert DataFormat.isASCII(s);
    }

}