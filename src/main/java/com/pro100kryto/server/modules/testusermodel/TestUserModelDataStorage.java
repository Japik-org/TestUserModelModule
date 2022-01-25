package com.pro100kryto.server.modules.testusermodel;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public final class TestUserModelDataStorage {
    public static final String KEY_ID = "id";
    public static final String KEY_NICKNAME = "nickname";
    public static final String KEY_PASS_HASH = "passHash";
    public static final String KEY_PASS_SALT = "passSalt";

    @Getter
    private final long userId;
    private final Map<Object, Object> keyValueMap = new HashMap<>(4);

    public TestUserModelDataStorage(long userId, String nickname,
                                    byte[] passHash, byte[] passSalt) {
        this.userId = userId;
        keyValueMap.put(KEY_ID, userId);
        keyValueMap.put(KEY_NICKNAME, nickname);
        keyValueMap.put(KEY_PASS_HASH, passHash);
        keyValueMap.put(KEY_PASS_SALT, passSalt);
    }

    public Object get(Object key) {
        return keyValueMap.get(key);
    }

    public void put(Object key, Object val) {
        keyValueMap.put(key, val);
    }
}
