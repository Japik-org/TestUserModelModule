package com.pro100kryto.server.modules.testusermodel;

import com.pro100kryto.server.livecycle.LiveCycleController;
import com.pro100kryto.server.module.*;
import com.pro100kryto.server.modules.usermodel.connection.IUserModelModuleConnection;
import com.pro100kryto.server.modules.crypt.connection.ICryptModuleConnection;
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

    public ICryptModuleConnection getPassCrypt() throws RemoteException {
        return passCryptModuleConnectionSafe.getModuleConnection();
    }

    @Override
    protected void setupSettingsBeforeInit() throws Throwable {
        settings.put(BaseModuleSettings.KEY_CONNECTION_MULTIPLE_ENABLED, false);
        settings.put(BaseModuleSettings.KEY_CONNECTION_CREATE_AFTER_INIT_ENABLED, true);

        super.setupSettingsBeforeInit();
    }

    @Override
    protected void setupLiveCycleControllerBeforeInit(LiveCycleController liveCycleController) {
        super.setupLiveCycleControllerBeforeInit(liveCycleController);

        liveCycleController.setInitImpl(() -> {
            final String passCryptModuleName = settings.getOrDefault("module-passCrypt", "passCrypt");
            initModuleOrWarn(passCryptModuleName);
            passCryptModuleConnectionSafe = setupModuleConnectionSafe(passCryptModuleName);
        });

        liveCycleController.setStartImpl(() -> {
            startModuleOrThrow(passCryptModuleConnectionSafe.getModuleName());
        });

        liveCycleController.setStopForceImpl(() -> {
            closeModuleConnection(passCryptModuleConnectionSafe);
        });

        liveCycleController.setDestroyImpl(() -> {
            passCryptModuleConnectionSafe = closeModuleConnection(passCryptModuleConnectionSafe);
        });
    }
}
