package com.app.server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RemoteServerImpl implements RemoteServer {

    private List<String> userNamesList = Collections.synchronizedList(new ArrayList<>());
    private List<String> changesList = Collections.synchronizedList(new ArrayList<>());

    @Override
    public String getRootPath() throws RemoteException {
        //return FileSystemView.getFileSystemView().getRoots()[0].getPath().toString();
        return File.listRoots()[0].getAbsolutePath();
    }

    @Override
    public void addUser(String userName) throws RemoteException {
        this.userNamesList.add(userName);
    }

    @Override
    public boolean containsUser(String userName) throws RemoteException {
        return this.userNamesList.contains(userName);
    }

    @Override
    public synchronized int getChangesLength() throws RemoteException {
        return this.changesList.size();
    }

    @Override
    public synchronized String getMessage(int num) throws RemoteException {
        return this.changesList.get(num);
    }

    @Override
    public synchronized void addMessage(String message) throws RemoteException{
        this.changesList.add(message);
    }

    @Override
    public void copy(String fromPath, String toPath) throws RemoteException {
        try {
            Files.copy(Paths.get(fromPath), Paths.get(toPath));
        } catch (IOException e) {

        }
    }

    @Override
    public boolean isDirectoryExist(String path) throws RemoteException {

        return !Files.notExists(Paths.get(path));
    }

    @Override
    public void makeDirectory(String path) throws RemoteException {
        try {
            Files.createDirectory(Paths.get(path));
        } catch (IOException e) {
            throw new RemoteException("IOException in md method", e);
        }
    }

    @Override
    public void block(String path, String username) throws RemoteException {


    }

    @Override
    public void unblock(String path, String user) throws RemoteException {

    }

    @Override
    public boolean isBlocked(String path) throws RemoteException {
        return false;
    }

    @Override
    public String[] blockedBy(String path) throws RemoteException {
        return new String[0];
    }

    @Override
    public void removeDirectory(String path, boolean isRecursive) throws RemoteException {

    }
}
