package com.pro100kryto.server.modules.testusermodel;

import com.pro100kryto.server.modules.usermodel.connection.IUserModelData;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import static com.pro100kryto.server.modules.testusermodel.TestUserModelDataStorage.*;

import java.rmi.RemoteException;

public final class TestUserModelDataAccess implements IUserModelData {
    private final IUserModelDataCallback callback;
    private TestUserModelDataStorage storage;
    @Getter
    private final long accessId;

    public TestUserModelDataAccess(IUserModelDataCallback callback, TestUserModelDataStorage storage, long accessId) {
        this.callback = callback;
        this.storage = storage;
        this.accessId = accessId;
    }

    @Getter
    private boolean closed = false;

    @Override
    public long getUserId() throws RemoteException {
        return storage.getUserId();
    }

    @Override
    public String getNickname() {
        return (String) getUserVal(KEY_NICKNAME);
    }

    @Override
    public void setNickname(String nickname) {
        setUserVal(KEY_NICKNAME, nickname);
    }

    @Override
    public byte[] getPassHash() {
        return (byte[]) getUserVal(KEY_PASS_HASH);
    }

    @Override
    public byte[] getPassSalt() {
        return (byte[]) getUserVal(KEY_PASS_SALT);
    }

    @Override
    public boolean checkPass(byte[] pass) throws RemoteException {
        return callback.getPassCrypt().check(
                getPassHash(),
                pass,
                getPassSalt()
        );
    }

    @Override
    public void setPass(byte[] pass, byte[] salt) throws RemoteException {
        setUserVal("passHash", callback.getPassCrypt().crypt(pass, salt));
        setUserVal("passSalt", salt);
    }

    @Override
    @Nullable
    public Object getUserVal(Object key) {
        if (closed){
            throw new IllegalStateException();
        }
        return storage.get(key);
    }

    @Override
    public void setUserVal(Object key, Object val) {
        if (closed){
            throw new IllegalStateException();
        }
        storage.put(key, val);
    }

    @Override
    public void close() {
        closed = true;
        callback.onCloseDataAccess(accessId);
        storage = null;
    }
}
