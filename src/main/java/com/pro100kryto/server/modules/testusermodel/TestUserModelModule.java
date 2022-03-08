package com.pro100kryto.server.modules.testusermodel;

import com.pro100kryto.server.livecycle.EmptyLiveCycleImpl;
import com.pro100kryto.server.livecycle.controller.ILiveCycleImplId;
import com.pro100kryto.server.livecycle.controller.LiveCycleController;
import com.pro100kryto.server.module.*;
import com.pro100kryto.server.modules.crypt.connection.ICryptModuleConnection;
import com.pro100kryto.server.modules.usermodel.connection.IUserModelModuleConnection;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.rmi.RemoteException;

public final class TestUserModelModule extends AModule<IUserModelModuleConnection> {

    private IModuleConnectionSafe<ICryptModuleConnection> passCryptModuleConnectionSafe;

    public TestUserModelModule(ModuleParams moduleParams) {
        super(moduleParams);
    }

    @Override
    public @NotNull IUserModelModuleConnection createModuleConnection(ModuleConnectionParams params) throws RemoteException {
        return new TestUserModelModuleConnection(this, params);
    }

    @Override
    protected void initLiveCycleController(LiveCycleController liveCycleController) {
        super.initLiveCycleController(liveCycleController);

        liveCycleController.putImplAll(new HighTestUserModelModuleLiveCycleImpl());
        liveCycleController.putImplAll(new LowTestUserModelModuleLiveCycleImpl());
    }

    public ICryptModuleConnection getPassCrypt() throws RemoteException {
        final ClassLoader cl = ICryptModuleConnection.class.getClassLoader();
        return passCryptModuleConnectionSafe.getModuleConnection();
    }

    private final class HighTestUserModelModuleLiveCycleImpl extends EmptyLiveCycleImpl implements ILiveCycleImplId {
        @Getter
        private final String name = "HighTestUserModelModuleLiveCycleImpl";
        @Getter @Setter
        private int priority = LiveCycleController.PRIORITY_HIGH;

        @Override
        public void init() throws Throwable {
            settings.put(BaseModuleSettings.KEY_CONNECTION_MULTIPLE_ENABLED, false);
            settings.put(BaseModuleSettings.KEY_CONNECTION_CREATE_AFTER_INIT_ENABLED, true);

            final String passCryptModuleName = settings.getOrDefault("module-passCrypt", "passCrypt");
            initModuleOrWarn(passCryptModuleName);
            passCryptModuleConnectionSafe = setupModuleConnectionSafe(passCryptModuleName);
        }

        @Override
        public void destroy() {
            passCryptModuleConnectionSafe = closeModuleConnection(passCryptModuleConnectionSafe);
        }
    }

    private final class LowTestUserModelModuleLiveCycleImpl extends EmptyLiveCycleImpl implements ILiveCycleImplId {
        @Getter
        private final String name = "LowTestUserModelModuleLiveCycleImpl";
        @Getter @Setter
        private int priority = LiveCycleController.PRIORITY_LOW;

        @Override
        public void init() throws Throwable {
            startModuleOrThrow(passCryptModuleConnectionSafe.getModuleName());
        }

        @Override
        public void stopForce() {
            closeModuleConnection(passCryptModuleConnectionSafe);
        }
    }
}
