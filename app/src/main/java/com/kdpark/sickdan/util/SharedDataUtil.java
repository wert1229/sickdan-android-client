package com.kdpark.sickdan.util;

import java.util.HashMap;
import java.util.Map;

public class SharedDataUtil {
    private static Map<String, String> sharedMap = new HashMap<>();

    public static final String JWT_INFO = "jwt.info";
    public static final String JWT_TOKEN = "jwt.token";
    public static final String STEP_INFO = "step.info";
    public static final String TODAY_COUNT = "today.count";

    public static void setData(String key, String value) {
        sharedMap.put(key, value);
    }

    public static String getData(String key, boolean isDrop) {
        String value = sharedMap.containsKey(key) ? sharedMap.get(key) : "";
        if (isDrop) sharedMap.remove(key);
        return value;
    }
}
