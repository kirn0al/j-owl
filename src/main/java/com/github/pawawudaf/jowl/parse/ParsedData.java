package com.github.pawawudaf.jowl.parse;

import java.util.Map;
import java.util.Set;
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

    public boolean isUrlContained(String link) {
        return data.containsKey(link);
    }

    public Set<Map.Entry<String, String>> getDataEntrySet() {
        return data.entrySet();
    }

}
