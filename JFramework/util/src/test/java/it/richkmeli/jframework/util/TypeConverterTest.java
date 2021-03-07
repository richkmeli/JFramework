package it.richkmeli.jframework.util;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TypeConverterTest {
    public static final String simpleJson = "{\"s4\":true,\"3\":\"v3\",\"s1\":\"v1\",\"s2\":3}";
    public static final String simpleMap = "{s4=true, 3=v3, s1=v1, s2=3}";
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

    @Test
    public void mapToJson() {
        Map map = new HashMap<Object, Object>();
        map.put("s1", "v1");
        map.put("s2", 3);
        map.put(3, "v3");
        map.put("s4", true);

        assertEquals(simpleMap, map.toString());

        String json = TypeConverter.mapToJson(map);
        assertEquals(simpleJson, json);
    }

}