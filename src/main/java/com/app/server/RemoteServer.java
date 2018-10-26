package com.app.server;

import java.nio.file.Path;
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

    void move(String fromPath, String toPath) throws RemoteException;

    boolean isDirectoryExist(String path) throws RemoteException;

    // md
    void makeDirectory(String path) throws RemoteException;

    // lock
    void block(Path path, String username) throws RemoteException;

    //unlock
    void unblock(Path path, String user) throws RemoteException;

    boolean isBlocked(Path path) throws RemoteException;

    String[] blockedBy(Path path) throws RemoteException;

    // rd
    boolean removeDirectory(Path path) throws RemoteException;

    //deltree
    boolean removeDirectoryRecursive(Path path) throws RemoteException;

    // del
    void removeFile(Path path) throws RemoteException;

    // mf
    void makeFile(Path path) throws RemoteException;

    String getDelimiter() throws RemoteException;

    // print
    String getFilesTree(String path) throws RemoteException;

    String[] getRoots() throws RemoteException;

    //dir
    String[] getFilesInDirectory(String path) throws RemoteException;

    void removeUser(String userName) throws RemoteException;
}
