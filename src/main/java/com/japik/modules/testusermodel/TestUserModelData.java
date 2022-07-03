package com.japik.modules.testusermodel;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class TestUserModelData {
    public static final String KEY_ID = "id";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASS_HASH = "passHash";
    public static final String KEY_PASS_SALT = "passSalt";

    @Getter
    private final long id;
    private final Map<Object, Object> keyValueMap = new HashMap<>(4);

    public TestUserModelData(long id, String nickname,
                             byte[] passHash, byte[] passSalt) {
        this.id = id;
        keyValueMap.put(KEY_ID, id);
        keyValueMap.put(KEY_USERNAME, nickname);
        keyValueMap.put(KEY_PASS_HASH, passHash);
        keyValueMap.put(KEY_PASS_SALT, passSalt);
    }

    public synchronized Object get(Object key) {
        return keyValueMap.get(key);
    }

    public synchronized void put(Object key, Object val) {
        keyValueMap.put(key, val);
    }

    public synchronized boolean contains(Object key) {
        return keyValueMap.containsKey(key);
    }
}
