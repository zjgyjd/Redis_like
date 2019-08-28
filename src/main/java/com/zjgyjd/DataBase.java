package com.zjgyjd;

import java.io.Serializable;
import java.util.*;

public class DataBase implements Serializable {
    private DataBase() {
        stringMap = new HashMap<>();
        hashes = new HashMap<>();
        lists = new HashMap<>();
        sets = new HashMap<>();
        zsets = new HashMap<>();
    }

    private static DataBase instance = new DataBase();

    public static DataBase getInstance() {
        return instance;
    }

    //String类型
    private Map<String, String> stringMap;

    //hash类型
    private Map<String, Map<String, String>> hashes;

    //list类型
    private Map<String, List<String>> lists;

    //set类型
    private Map<String, Set<String>> sets;

    //zset类型
    private Map<String, LinkedHashSet<String>> zsets;

    public Map<String, Map<String, String>> getHashes() {
        return hashes;
    }

    public List<String> getList(String key) {
        return lists.computeIfAbsent(key, k -> new ArrayList<>());
    }

    public Map<String, String> getHashMap(String key) {
        return hashes.computeIfAbsent(key, k -> new HashMap<>());
    }
}
