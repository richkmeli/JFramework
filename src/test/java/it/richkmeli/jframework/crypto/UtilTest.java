package it.richkmeli.jframework.crypto;

import it.richkmeli.jframework.crypto.util.RandomStringGenerator;
import it.richkmeli.jframework.crypto.util.TypeConverter;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;

import static org.junit.Assert.assertEquals;


public class UtilTest {
    @Test
    public void GenerateAlphanumericString() {
        int[] lenghts = {8, 10, 100};

        for (int i : lenghts) {
            String randomString = RandomStringGenerator.GenerateAlphanumericString(i);
            assertEquals(i, randomString.length());
        }

    }

    /*
    @Test
    public void GenerateUnboundedString() {
        int[] lenghts = {8,10,100};

        for (int info : lenghts) {
            String randomString = RandomStringGenerator.GenerateUnboundedString(info);
            assertEquals(info, randomString.length());
        }
    }
*/

    @Test
    public void GenerateBoundedString() {
        int[] lenghts = {8, 10, 100};

        for (int i : lenghts) {
            String randomString = RandomStringGenerator.GenerateBoundedString(i, 0, 255);
            assertEquals(i, randomString.length());
        }


    }


    @Test
    public void bytesToHex() {
        int[] lenghts = {8, 10, 100};

        for (int i : lenghts) {
            String randomString = RandomStringGenerator.GenerateBoundedString(i, 0, 255);
            String hex = TypeConverter.bytesToHex(randomString.getBytes());
            //String hex2 = TypeConverter.bytesToHex2(randomString.getBytes());
            //System.out.println(hex);

            assertEquals(hex, DatatypeConverter.printHexBinary(randomString.getBytes()).toLowerCase());
        }


    }


}