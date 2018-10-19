package com.app.client;

import com.app.server.RemoteServer;

import java.rmi.RemoteException;

public class CommandExecutor {

    private static final String[] COMMANDS = {"cd", "md", "rd", "deltree", "mf", "del", "lock", "unlock", "move", "print"};

    private String currentPath;
    private RemoteServer server;

    public CommandExecutor(RemoteServer server) {
        this.server = server;
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }

    public void execute(String[] commands) {
        String msg = "";



        if (!msg.equals("")) {
            try {
                server.addMessage(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }


}
