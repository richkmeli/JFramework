package it.richkmeli.jframework.util;

import org.junit.Assert;
import org.junit.Test;

import javax.xml.crypto.Data;

import static org.junit.Assert.*;

public class DataFormatTest {

    @Test
    public void isJSONValid() {
        String jsonInput = "{" +
                "    \"channel\": \"richkware\"," +
                "    \"data0\": \"BbVMwNMgUtglVgJx\"" +
                "}";
        Assert.assertTrue(DataFormat.isJSONValid(jsonInput));
    }

    @Test
    public void isJSONValidError() {
        String jsonInput = "{" +
                "    channel: richkware," +
                "    \"data0: BbVMwNMgUtglVgJx" +
                "}";
        Assert.assertFalse(DataFormat.isJSONValid(jsonInput));
    }
}