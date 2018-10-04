package app.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteServer extends Remote {

    String getRootPath() throws RemoteException;

    void addUser(String userName) throws RemoteException;

    boolean containsUser(String userName) throws RemoteException;

    int getChangesLength() throws RemoteException;

    String getMessage(int num) throws RemoteException;

    void addMessage(String message) throws RemoteException;

    void copy(String fromPath, String toPath) throws RemoteException;

    boolean isDirectoryExist(String path) throws RemoteException;

    void makeDirectory(String path) throws RemoteException;

    void block(String path, String username) throws RemoteException;

    void unblock(String path, String user) throws RemoteException;

    boolean isBlocked(String path) throws RemoteException;

    String[] blockedBy(String path) throws RemoteException;

    void removeDirectory(String path, boolean isRecursive) throws RemoteException;


}
