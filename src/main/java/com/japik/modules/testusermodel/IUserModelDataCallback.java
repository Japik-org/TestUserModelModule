package com.japik.modules.testusermodel;

import com.japik.modules.crypt.connection.ICryptModuleConnection;

import java.rmi.RemoteException;

public interface IUserModelDataCallback {
    ICryptModuleConnection getPassCrypt() throws RemoteException;
}
