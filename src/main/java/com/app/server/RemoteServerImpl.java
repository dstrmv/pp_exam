package com.app.server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
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
    public synchronized void addMessage(String message) throws RemoteException {
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
        return Files.exists(Paths.get(path)) && Files.isDirectory(Paths.get(path));
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

        Path path1 = Paths.get(path);
        boolean blocked = isBlocked(path);

        if (blocked) {
            try {
                String[] blockedBy = blockedBy(path);
                String[] newBlockedBy = new String[blockedBy.length + 1];
                System.arraycopy(blockedBy, 0, newBlockedBy, 0, blockedBy.length);
                newBlockedBy[newBlockedBy.length - 1] = username;
                Files.setAttribute(path1, "user:blockedby", newBlockedBy);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Files.setAttribute(path1, "user:blocked", true);
                Files.setAttribute(path1, "user:blockedBy", new String[]{username});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void unblock(String path, String user) throws RemoteException {

        Path path1 = Paths.get(path);
        boolean blocked = isBlocked(path);

        if (blocked) {
            try {
                String[] blockedBy = blockedBy(path);
                String[] newBlockedBy = Arrays.stream(blockedBy).filter(userattr -> !user.equals(userattr)).toArray(String[]::new);

                if (newBlockedBy.length == 0) {
                    Files.setAttribute(path1, "user:blocked", false);
                }
                Files.setAttribute(path1, "user:blockedby", newBlockedBy);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public boolean isBlocked(String path) throws RemoteException {

        Path path1 = Paths.get(path);
        boolean blocked = false;

        try {
            blocked = (boolean) Files.getAttribute(path1, "user:blocked");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException | UnsupportedOperationException e) {
            blocked = false;
        }
        return blocked;
    }

    @Override
    public String[] blockedBy(String path) throws RemoteException {
        Path path1 = Paths.get(path);
        String[] blockedBy = { };
        try {
            blockedBy = (String[]) Files.getAttribute(path1, "user:blockedBy");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return blockedBy;
    }

    @Override
    public void removeDirectory(String path) throws RemoteException {

    }

    @Override
    public String getDelimiter() throws RemoteException {
        return File.separator;
    }


}
