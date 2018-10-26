package com.app.server;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.rmi.RemoteException;
import java.util.*;

public class RemoteServerImpl implements RemoteServer {

    private List<String> userNamesList = Collections.synchronizedList(new ArrayList<>());
    private List<String> changesList = Collections.synchronizedList(new ArrayList<>());

    private static String getIndentString(int indent) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append("-");
        }
        return sb.toString();
    }

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

    //TODO
    @Override
    public void block(String path, String username) throws RemoteException {

        Path path1 = Paths.get(path);
        boolean blocked = isBlocked(path);



        if (blocked) {
            try {
                String[] blockedBy = blockedBy(path);
                if (Arrays.asList(blockedBy).contains(username)) {
                    return;
                }
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

    //TODO VOT ETO
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

        String name = "user.blocked";

        UserDefinedFileAttributeView view =
                Files.getFileAttributeView(path1, UserDefinedFileAttributeView.class);
        try {

            if (view.list().isEmpty()) {
                return false;
            }

            ByteBuffer buffer = ByteBuffer.allocate(view.size(name));
            view.read(name, buffer);
            buffer.flip();
            String value = Charset.defaultCharset().decode(buffer).toString();
            if (value.equals("true")) {
                blocked = true;
            }
        } catch (IOException e) {
            throw new RemoteException();
        }

        return blocked;
    }


    @Override
    public String[] blockedBy(String path) throws RemoteException {
        Path path1 = Paths.get(path);
        String[] blockedBy = {};

        String name = "user.blockedBy";

        UserDefinedFileAttributeView view =
                Files.getFileAttributeView(path1, UserDefinedFileAttributeView.class);

        if (!isBlocked(path)) {
            return blockedBy;
        }

        try {
            ByteBuffer buffer = ByteBuffer.allocate(view.size(name));
            view.read(name, buffer);
            buffer.flip();
            String value = Charset.defaultCharset().decode(buffer).toString();
            blockedBy = value.split(";");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return blockedBy;
    }

    @Override
    public boolean removeDirectory(String path) throws RemoteException {

        Path path1 = Paths.get(path);
        if (Files.exists(path1) && Files.isDirectory(path1)) {
            if (!isBlocked(path)) {
                if (Objects.requireNonNull(path1.toFile().list()).length == 0) {
                    try {
                        Files.delete(path1);
                        return true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String getDelimiter() throws RemoteException {
        return File.separator;
    }

    @Override
    public void removeDirectoryRecursive(String path) throws RemoteException {

    }

    @Override
    public void removeFile(String path) throws RemoteException {

    }

    @Override
    public void makeFile(String path) throws RemoteException {
        try {
            Files.createFile(Paths.get(path));
        } catch (IOException e) {
            throw new RemoteException("IOException");
        }
    }

    @Override
    public String getFilesTree(String path) throws RemoteException {
        File folder = Paths.get(path).toFile();
        if (!folder.isDirectory()) {
            throw new RemoteException("file is not a directory");
        }
        int indent = 0;
        StringBuilder sb = new StringBuilder();
        printDirectoryTree(folder, indent, sb);
        return sb.toString();
    }

    private void printDirectoryTree(File folder, int indent, StringBuilder sb) throws RemoteException {
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("file is not a Directory");
        }
        sb.append(getIndentString(indent));
        sb.append("-");
        sb.append(folder.getName());
        sb.append(File.separator);
        if (isBlocked(folder.getPath())) {
            sb.append(" ").append("LOCKED BY: ").append(Arrays.toString(this.blockedBy(folder.getPath())));
        }
        sb.append("\n");
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                printDirectoryTree(file, indent + 1, sb);
            } else {
                printFile(file, indent + 1, sb);
            }
        }
    }

    private void printFile(File file, int indent, StringBuilder sb) throws RemoteException {
        sb.append(getIndentString(indent));
        sb.append("-");
        sb.append(file.getName());
        if (isBlocked(file.getPath())) {
            sb.append(" ").append("LOCKED BY: ").append(Arrays.toString(this.blockedBy(file.getPath())));
        }
        sb.append("\n");
    }

    public String[] getRoots() {
        return Arrays.stream(File.listRoots())
                .map(File::getAbsolutePath)
                .toArray(String[]::new);
    }

    @Override
    public String[] getFilesInDirectory(String path) throws RemoteException {
        Path path1 = Paths.get(path);
        return path1.toFile().list();
    }
}
