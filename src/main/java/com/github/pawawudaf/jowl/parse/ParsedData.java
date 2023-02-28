package com.github.pawawudaf.jowl.parse;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ParsedData {

    private ConcurrentMap<String, String> data;

    public ParsedData() {
        data = new ConcurrentHashMap<>();
    }

    public void putObject(String key, String value) {
        data.put(key, value);
    }

    public String getValue(String key) {
        return data.get(key);
    }

}
