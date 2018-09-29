package app.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscriber;

public interface RemoteServer extends Remote {

    String getRootPath() throws RemoteException;
    void addUser(String userName) throws RemoteException;
    boolean containsUser(String userName) throws RemoteException;
    int getChangesLength() throws RemoteException;
    String getMessage(int num) throws RemoteException;
    public void addMessage(String message) throws RemoteException;

}
