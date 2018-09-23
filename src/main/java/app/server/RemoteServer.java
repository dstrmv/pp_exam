package app.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteServer extends Remote {

    String getRootPath() throws RemoteException;
    void addUser(String userName) throws RemoteException;
    boolean containsUser(String userName) throws RemoteException;

}
