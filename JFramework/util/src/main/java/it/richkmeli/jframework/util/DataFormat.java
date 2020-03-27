package it.richkmeli.jframework.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
}
