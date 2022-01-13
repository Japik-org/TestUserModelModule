package com.pro100kryto.server.modules.testusermodel;

import com.pro100kryto.server.module.AModule;
import com.pro100kryto.server.module.ModuleParams;
import com.pro100kryto.server.modules.usermodel.connection.IUserModelModuleConnection;
import org.jetbrains.annotations.NotNull;

public final class TestUsersModelModule extends AModule<IUserModelModuleConnection> {

    public TestUsersModelModule(ModuleParams moduleParams) {
        super(moduleParams);
    }

    @Override
    public @NotNull IUserModelModuleConnection createModuleConnection() {
        return null;
    }
}
