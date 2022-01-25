package com.pro100kryto.server.modules.testusermodel;

import com.google.common.hash.Hashing;
import com.pro100kryto.server.module.AModuleConnection;
import com.pro100kryto.server.module.ModuleConnectionException;
import com.pro100kryto.server.module.ModuleConnectionParams;
import com.pro100kryto.server.modules.usermodel.connection.IUserModelData;
import com.pro100kryto.server.modules.usermodel.connection.IUserModelModuleConnection;
import com.pro100kryto.server.modules.usermodel.connection.UserNotFoundException;
import com.pro100kryto.server.modules.crypt.connection.ICryptModuleConnection;
import org.eclipse.collections.impl.map.mutable.primitive.LongObjectHashMap;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.util.concurrent.atomic.AtomicLong;

import static com.pro100kryto.server.modules.testusermodel.Crypt.*;

public class TestUserModelModuleConnection extends AModuleConnection<TestUserModelModule, IUserModelModuleConnection>
        implements IUserModelModuleConnection, IUserModelDataCallback {

    private final LongObjectHashMap<TestUserModelDataAccess> accessIdDataAccessMap = new LongObjectHashMap<>(64);
    private final LongObjectHashMap<TestUserModelDataStorage> userIdDataStorageMap = new LongObjectHashMap<>(64);

    private final AtomicLong accessIdCounter = new AtomicLong(0);
    private final AtomicLong userIdCounter = new AtomicLong(0);

    private final byte[] clientSalt = "LN9tFnhm".getBytes(StandardCharsets.UTF_8);

    private final String userTesterName = "Tester";
    private final byte[] userTesterPass = "1234".getBytes(StandardCharsets.UTF_8);

    public TestUserModelModuleConnection(@NotNull TestUserModelModule module, ModuleConnectionParams params)
            throws RemoteException {

        super(module, params);

        createUser(userTesterName,
                Hashing.sha512().hashBytes(
                        mergeBytes(userTesterPass, clientSalt)
                ).asBytes()
        );
    }

    @Override
    public IUserModelData createUser(String nickname, byte[] pass) throws RemoteException {
        try {
            final byte[] userSalt = module.getPassCrypt().randomSalt();

            final TestUserModelDataStorage userDataStorage = new TestUserModelDataStorage(
                    userIdCounter.getAndIncrement(),
                    userTesterName,
                    module.getPassCrypt().crypt(
                            pass,
                            userSalt
                    ),
                    userSalt
            );
            userIdDataStorageMap.put(userDataStorage.getUserId(), userDataStorage);

            return createAccess(userDataStorage);

        } catch (Throwable throwable){
            throw new ModuleConnectionException(
                    module.getService().getName(),
                    getModuleName(),
                    throwable
            );
        }
    }

    @Override
    public boolean existsUserByUserId(long userId) {
        return userIdDataStorageMap.containsKey(userId);
    }

    @Override
    public boolean existsUserByKeyVal(Object key, Object val) {
        for (final TestUserModelDataStorage userDataStorage : userIdDataStorageMap) {
            final Object val2 = userDataStorage.get(key);
            if (val2 == null) continue;
            if (val.equals( val2 )){
                return true;
            }
        }
        return false;
    }

    @Override
    public IUserModelData getUserByUserId(long userId) throws UserNotFoundException {
        final TestUserModelDataStorage userDataStorage = userIdDataStorageMap.get(userId);
        if (userDataStorage == null) {
            throw new UserNotFoundException("userId", userId);
        }

        return createAccess(userDataStorage);
    }

    @Override
    public IUserModelData getUserByKeyVal(Object key, Object val) throws UserNotFoundException {
        for (final TestUserModelDataStorage userDataStorage : userIdDataStorageMap) {
            final Object val2 = userDataStorage.get(key);
            if (val2 == null) continue;
            if (val.equals( val2 )){
                return createAccess(userDataStorage);
            }
        }
        throw new UserNotFoundException(key, val);
    }

    @Override
    public ICryptModuleConnection getPassCrypt() throws RemoteException {
        return module.getPassCrypt();
    }

    @Override
    public void onCloseDataAccess(long accessId) {
        accessIdDataAccessMap.remove(accessId);
    }

    private IUserModelData createAccess(TestUserModelDataStorage userDataStorage) {
        final TestUserModelDataAccess userModelDataAccess = new TestUserModelDataAccess(
                this,
                userDataStorage,
                accessIdCounter.getAndIncrement()
        );
        accessIdDataAccessMap.put(userModelDataAccess.getAccessId(), userModelDataAccess);
        return userModelDataAccess;
    }
}
