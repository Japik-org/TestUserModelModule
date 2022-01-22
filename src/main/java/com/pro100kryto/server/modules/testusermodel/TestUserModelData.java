package com.pro100kryto.server.modules.testusermodel;

import com.pro100kryto.server.modules.usermodel.connection.IUserModelData;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.pro100kryto.server.modules.testusermodel.Crypt.*;

@Getter
public final class TestUserModelData implements IUserModelData {
    public static final String KEY_ID = "id";
    public static final String KEY_NICKNAME = "nickname";
    public static final String KEY_PASS_HASH = "passHash";
    public static final String KEY_PASS_SALT = "passSalt";

    private final byte[] localSalt;

    private final long userId;
    private final Map<Object, Object> keyValueMap = new HashMap<>(4); // database emulator

    private final byte[] secret;

    private boolean closed = false;

    public TestUserModelData(byte[] localSalt,
                             long userId, String nickname,
                             byte[] passHash, byte[] passSalt) {
        this.localSalt = localSalt;

        this.userId = userId;
        keyValueMap.put(KEY_ID, userId);
        keyValueMap.put(KEY_NICKNAME, nickname);
        keyValueMap.put(KEY_PASS_HASH, passHash);
        keyValueMap.put(KEY_PASS_SALT, passSalt);

        secret = composeSalt(
                ByteBuffer.allocate(Long.SIZE/8)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .putLong(userId)
                    .array(),
                "123".getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    public String getNickname() {
        return (String) keyValueMap.get(KEY_NICKNAME);
    }

    @Override
    public void setNickname(String nickname) {
        keyValueMap.put(KEY_NICKNAME, nickname);
    }

    @Override
    public byte[] getPassHash() {
        return (byte[]) keyValueMap.get(KEY_PASS_HASH);
    }

    @Override
    public byte[] getPassSalt() {
        return (byte[]) keyValueMap.get(KEY_PASS_SALT);
    }

    @Override
    public boolean checkPass(byte[] pass) {
        return Arrays.equals(
                getPassHash(),
                createUserPass(
                    pass,
                    getPassSalt(),
                    localSalt
                )
        );
    }

    @Override
    public void setPass(byte[] pass, byte[] salt) {
        keyValueMap.put("passHash", createUserPass(pass, salt, localSalt));
        keyValueMap.put("passSalt", salt);
    }

    @Override
    public boolean checkSign(byte[] sign, byte[] src) {
        return checkUserSign(sign, secret, src);
    }

    @Override
    @Nullable
    public Object getUserVal(Object key) {
        return keyValueMap.get(key);
    }

    @Override
    public void setUserVal(Object key, Object val) {
        keyValueMap.put(key, val);
    }

    @Override
    public void close() {
        closed = true;
        keyValueMap.clear();
    }
}
