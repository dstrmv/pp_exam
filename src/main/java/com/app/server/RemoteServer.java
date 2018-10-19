package com.app.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteServer extends Remote {

    String getRootPath() throws RemoteException;

    void addUser(String userName) throws RemoteException;

    boolean containsUser(String userName) throws RemoteException;

    int getChangesLength() throws RemoteException;

    String getMessage(int num) throws RemoteException;

    void addMessage(String message) throws RemoteException;

    // move
    void copy(String fromPath, String toPath) throws RemoteException;

    boolean isDirectoryExist(String path) throws RemoteException;

    // md
    void makeDirectory(String path) throws RemoteException;

    // lock
    void block(String path, String username) throws RemoteException;

    //unlock
    void unblock(String path, String user) throws RemoteException;

    boolean isBlocked(String path) throws RemoteException;

    String[] blockedBy(String path) throws RemoteException;

    // rd
    void removeDirectory(String path) throws RemoteException;

    //deltree
    void removeDirectoryRecursive(String path) throws RemoteException;

    // del
    void removeFile(String path) throws RemoteException;

    // mf
    void makeFile(String path) throws RemoteException;

    String getDelimiter() throws RemoteException;

    // print
    String getFilesTree(String path) throws RemoteException;


}
