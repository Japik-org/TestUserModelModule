package com.pro100kryto.server.modules.testusermodel;

import com.pro100kryto.server.module.AModuleConnection;
import com.pro100kryto.server.module.ModuleConnectionParams;
import com.pro100kryto.server.modules.usermodel.connection.IUserModelData;
import com.pro100kryto.server.modules.usermodel.connection.IUserModelModuleConnection;
import com.pro100kryto.server.modules.usermodel.connection.UserNotFoundException;
import org.eclipse.collections.impl.map.mutable.primitive.LongObjectHashMap;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicLong;

import static com.pro100kryto.server.modules.testusermodel.Crypt.*;

public class TestUserModelModuleConnection extends AModuleConnection<TestUserModelModule, IUserModelModuleConnection>
        implements IUserModelModuleConnection {

    private final LongObjectHashMap<TestUserModelData> userIdDataMap = new LongObjectHashMap<>(64);

    private final AtomicLong userIdCounter = new AtomicLong(0);

    private final int randomSaltLen = 64;
    private final byte[] clientSalt = "LN9tFnhm".getBytes(StandardCharsets.UTF_8);
    private final byte[] localSalt = "JwfKT1Wj".getBytes(StandardCharsets.UTF_8);

    private final String userTesterName = "Tester";
    private final String userTesterPass = "1234";

    public TestUserModelModuleConnection(@NotNull TestUserModelModule module, ModuleConnectionParams params) {
        super(module, params);

        createUser(userTesterName,
                hashPass(
                        userTesterPass.getBytes(StandardCharsets.UTF_8),
                        clientSalt
                )
        );
    }

    @Override
    public IUserModelData createUser(String nickname, byte[] pass) {
        final byte[] userSalt = newRandomSalt(randomSaltLen);

        final TestUserModelData userModelData = new TestUserModelData(
                localSalt,
                userIdCounter.getAndIncrement(),
                userTesterName,
                hashPass(
                        pass,
                        composeSalt(localSalt, userSalt)
                ),
                userSalt
        );

        userIdDataMap.put(userModelData.getUserId(), userModelData);

        return userModelData;
    }

    @Override
    public boolean existsUserByUserId(long userId) {
        return userIdDataMap.containsKey(userId);
    }

    @Override
    public boolean existsUserByKeyVal(Object key, Object val) {
        for (final TestUserModelData userModelData : userIdDataMap) {
            final Object val2 = userModelData.getUserVal(key);
            if (val2 == null) continue;
            if (val.equals( val2 )){
                return true;
            }
        }
        return false;
    }

    @Override
    public IUserModelData getUserByUserId(long userId) throws UserNotFoundException {
        final IUserModelData userModelData = userIdDataMap.get(userId);
        if (userModelData == null){
            throw new UserNotFoundException("userId", userId);
        }
        return userModelData;
    }

    @Override
    public IUserModelData getUserByKeyVal(Object key, Object val) throws UserNotFoundException {
        for (final TestUserModelData userModelData : userIdDataMap) {
            final Object val2 = userModelData.getUserVal(key);
            if (val2 == null) continue;
            if (val.equals( val2 )){
                return userModelData;
            }
        }
        throw new UserNotFoundException(key, val);
    }
}
