package it.richkmeli.jframework.crypto.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RandomStringGeneratorTest {
    private static int[] lengths = {8, 10, 100, 1000};

    //    @Test
//    public void generateUtf16String() {
//        for (int i : lengths) {
//            String randomString = RandomStringGenerator.generateUtf16String(i);
//            assertEquals(i, randomString.length());
//        }
//    }
//    @Test
//    public void generateUtf8String() {
//        for (int i : lengths) {
//            String randomString = RandomStringGenerator.generateUtf8String(i);
//            assertEquals(i, randomString.length());
//        }
//    }
    @Test
    public void generateASCIItring() {
        for (int i : lengths) {
            String randomString = RandomStringGenerator.generateASCIItring(i);
            assertEquals(i, randomString.length());
        }
    }

    @Test
    public void generateAlphanumericString() {
        for (int i : lengths) {
            String randomString = RandomStringGenerator.generateAlphanumericString(i);
            assertEquals(i, randomString.length());
        }

    }

    @Test
    public void generateBoundedString() {
        for (int i : lengths) {
            String randomString = RandomStringGenerator.generateBoundedString(i, 0, 255);
            assertEquals(i, randomString.length());
        }
    }
}