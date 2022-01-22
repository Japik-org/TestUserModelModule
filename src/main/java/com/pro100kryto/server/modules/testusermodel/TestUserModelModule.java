package com.pro100kryto.server.modules.testusermodel;

import com.pro100kryto.server.module.AModule;
import com.pro100kryto.server.module.BaseModuleSettings;
import com.pro100kryto.server.module.ModuleConnectionParams;
import com.pro100kryto.server.module.ModuleParams;
import com.pro100kryto.server.modules.usermodel.connection.IUserModelModuleConnection;
import org.jetbrains.annotations.NotNull;

public final class TestUserModelModule extends AModule<IUserModelModuleConnection> {

    public TestUserModelModule(ModuleParams moduleParams) {
        super(moduleParams);
    }

    @Override
    public @NotNull IUserModelModuleConnection createModuleConnection(ModuleConnectionParams params) {
        return new TestUserModelModuleConnection(this, params);
    }

    @Override
    protected void setupSettingsBeforeInit() throws Throwable {
        settings.put(BaseModuleSettings.KEY_CONNECTION_MULTIPLE_ENABLED, false);
        settings.put(BaseModuleSettings.KEY_CONNECTION_CREATE_AFTER_INIT_ENABLED, true);

        super.setupSettingsBeforeInit();
    }
}
