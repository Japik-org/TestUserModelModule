package com.pro100kryto.server.modules.testusermodel;

import com.pro100kryto.server.modules.crypt.connection.ICryptModuleConnection;

import java.rmi.RemoteException;

public interface IUserModelDataCallback {
    ICryptModuleConnection getPassCrypt() throws RemoteException;
}
