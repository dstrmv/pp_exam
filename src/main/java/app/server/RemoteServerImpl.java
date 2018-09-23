package app.server;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RemoteServerImpl implements RemoteServer {

    private List<String> userNamesList = Collections.synchronizedList(new ArrayList<>());

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
}
