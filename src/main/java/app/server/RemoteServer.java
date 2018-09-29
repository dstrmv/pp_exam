package app.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscriber;

public interface RemoteServer extends Remote {

    String getRootPath() throws RemoteException;
    void addUser(String userName) throws RemoteException;
    void addSubscriber(Flow.Subscriber<String> subscriber) throws RemoteException;
    boolean containsUser(String userName) throws RemoteException;
    void addMessage(String s) throws RemoteException;
}
