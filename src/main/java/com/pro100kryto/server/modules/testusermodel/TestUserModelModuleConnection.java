package com.pro100kryto.server.modules.testusermodel;

import com.pro100kryto.server.module.AModuleConnection;
import com.pro100kryto.server.module.ModuleConnectionParams;
import com.pro100kryto.server.modules.crypt.connection.ICryptModuleConnection;
import com.pro100kryto.server.modules.usermodel.connection.IUserModel;
import com.pro100kryto.server.modules.usermodel.connection.IUserModelModuleConnection;
import com.pro100kryto.server.modules.usermodel.connection.UserAlreadyExistsException;
import com.pro100kryto.server.modules.usermodel.connection.UserNotFoundException;
import org.eclipse.collections.impl.map.mutable.primitive.LongObjectHashMap;
import org.jetbrains.annotations.NotNull;

import java.rmi.RemoteException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.pro100kryto.server.modules.testusermodel.TestUserModelData.KEY_USERNAME;

public final class TestUserModelModuleConnection extends AModuleConnection<TestUserModelModule, IUserModelModuleConnection>
        implements IUserModelModuleConnection, IUserModelDataCallback {

    private final LongObjectHashMap<TestUserModelData> userIdDataMap = new LongObjectHashMap<>(64);
    private final AtomicInteger userIdCounter = new AtomicInteger(0);

    public TestUserModelModuleConnection(@NotNull TestUserModelModule module, ModuleConnectionParams params)
            throws RemoteException {
        super(module, params);
    }

    @Override
    public synchronized IUserModel createUser(String username, byte[] pass) throws RemoteException, UserAlreadyExistsException {
        if (userIdDataMap.values().stream().anyMatch(data -> Objects.equals(data.get(KEY_USERNAME), username))) {
            throw new UserAlreadyExistsException(KEY_USERNAME, username);
        }

        final byte[] userSalt = module.getPassCrypt().randomSalt(Consts.SALT_LEN);
        final TestUserModelData userData = new TestUserModelData(
                userIdCounter.getAndIncrement(),
                username,
                module.getPassCrypt().crypt(
                        pass,
                        userSalt
                ),
                userSalt
        );

        userIdDataMap.put(userData.getId(), userData);
        return getUserModel(userData);
    }

    @Override
    public boolean existsUserByUserId(long userId) {
        return userIdDataMap.containsKey(userId);
    }

    @Override
    public boolean existsUserByKeyVal(Object key, Object val) {
        return userIdDataMap.values().stream()
                .anyMatch(data -> Objects.equals(data.get(key), val));
    }

    @Override
    public IUserModel getUserByUserId(long userId) throws UserNotFoundException {
        final TestUserModelData userData = userIdDataMap.get(userId);
        if (userData == null) {
            throw new UserNotFoundException(TestUserModelData.KEY_ID, userId);
        }
        return getUserModel(userData);
    }

    @Override
    public IUserModel getOneUserByKeyVal(Object key, Object val) throws UserNotFoundException {
        for (final TestUserModelData userData : userIdDataMap) {
            final Object val2 = userData.get(key);
            if (val2 == null) continue;
            if (val.equals( val2 )){
                return getUserModel(userData);
            }
        }
        throw new UserNotFoundException(key, val);
    }

    @Override
    public Iterable<IUserModel> getUsersByKeyVal(Object key, Object val) throws RemoteException {
        return userIdDataMap.values().stream()
                .filter(data -> Objects.equals(data.get(key), val))
                .map(this::getUserModel)
                .collect(Collectors.toList());
    }

    // callback

    @Override
    public ICryptModuleConnection getPassCrypt() throws RemoteException {
        return module.getPassCrypt();
    }

    // private

    private IUserModel getUserModel(TestUserModelData userData) {
        return new TestUserModel(
                this,
                userData
        );
    }
}
