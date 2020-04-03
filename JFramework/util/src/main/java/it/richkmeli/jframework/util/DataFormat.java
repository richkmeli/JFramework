package it.richkmeli.jframework.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class DataFormat {
    public static boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public static boolean isASCII(String s) {
        return StandardCharsets.US_ASCII.newEncoder().canEncode(s);
    }

    public static boolean isUTF8(String s) {
        return StandardCharsets.UTF_8.newEncoder().canEncode(s);
    }

    public static boolean isUTF16(String s) {
        return StandardCharsets.UTF_16.newEncoder().canEncode(s);
    }
}
