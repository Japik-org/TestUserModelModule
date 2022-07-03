package com.japik.modules.testusermodel;

import com.japik.modules.usermodel.connection.IUserModel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.rmi.RemoteException;
import java.util.Map;

import static com.japik.modules.testusermodel.TestUserModelData.*;

public class TestUserModel implements IUserModel {
    private final IUserModelDataCallback callback;
    private TestUserModelData data;

    public TestUserModel(IUserModelDataCallback callback, TestUserModelData data) {
        this.callback = callback;
        this.data = data;
    }

    @Getter
    private boolean closed = false;

    @Override
    public long getId() throws RemoteException {
        return data.getId();
    }

    @Override
    public String getUsername() {
        return (String) getVal(KEY_USERNAME);
    }

    @Override
    public void setUsername(String username) {
        setVal(KEY_USERNAME, username);
    }

    @Override
    public void setPass(byte[] pass) throws RemoteException {
        final byte[] salt = callback.getPassCrypt().randomSalt(Consts.SALT_LEN);
        setVal("passHash", callback.getPassCrypt().crypt(pass, salt));
        setVal("passSalt", salt);
    }

    @Override
    public synchronized boolean checkPass(byte[] pass) throws RemoteException {
        return callback.getPassCrypt().check(
                (byte[]) getVal(KEY_PASS_HASH),
                pass,
                (byte[]) getVal(KEY_PASS_SALT)
        );
    }

    @Override
    @Nullable
    public synchronized Object getVal(Object key) {
        if (closed) throw new IllegalStateException();
        return data.get(key);
    }

    @Override
    public synchronized void setVal(@NotNull Object key, Object val) {
        if (closed) throw new IllegalStateException();
        data.put(key, val);
    }

    @Override
    public synchronized void setAllVal(Map<Object, Object> values) throws RemoteException {
        if (closed) throw new IllegalStateException();
        values.forEach((k,v) -> data.put(k,v));
    }

    @Override
    public synchronized void close() {
        data = null;
        closed = true;
    }
}
