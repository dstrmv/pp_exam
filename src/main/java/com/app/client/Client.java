package com.app.client;

import com.app.server.RemoteServer;

import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Client implements Runnable {

    private static final String NO_PATH = "";
    private static final String NO_NAME = "";


    private String currentPath;
    private String prompt;
    private String name;
    private ClientConnector connector;
    private RemoteServer server;
    private CommandExecutor executor;
    private boolean connected;

    Client() {
        currentPath = NO_PATH;
        name = NO_NAME;
    }

    public void run() {

        connector = new ClientConnector();
        Scanner in = new Scanner(System.in);
        while (!connected) {

            String connectionInputMessage = in.nextLine();
            String[] args = Parser.parse(connectionInputMessage);

            String command = args[0];
            String[] addressPort = args[1].split(":");
            String address = addressPort[0];
            int port = Integer.parseInt(addressPort[1]);
            String userName = args[2];

            if (!userName.matches("[a-zA-Z]+")) {
                System.out.println("username must be only english letters");
                continue;
            }

            if (!command.toLowerCase().equals("connect") || !command.toLowerCase().equals("quit")) {
                System.out.println("pls connect at first");
            }

            if (command.toLowerCase().equals("quit")) {
                System.exit(0);
            }

            try {
                server = connector.connect(address, port, "server");
                if (!server.containsUser(userName)) {
                    server.addUser(userName);

                } else {
                    throw new Exception("user already added");
                }

                currentPath = server.getRootPath();
                prompt = currentPath + ">";
                connected = true;

                Thread changesListener = new Thread(() -> {
                    try {
                        int currentMsgNum = server.getChangesLength();
                        int actualMsgNum;
                        while (connected) {
                            actualMsgNum = server.getChangesLength();
                            if (actualMsgNum - currentMsgNum > 0) {
                                System.out.println("\n" + server.getMessage(currentMsgNum++));
                            } else {
                                try {
                                    TimeUnit.SECONDS.sleep(1);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    } catch (RemoteException e) {
                        System.out.println("something is wrong");
                    }
                });

                changesListener.setDaemon(true);
                changesListener.start();

            } catch (Exception e) {
                System.out.println("Connection failed.");
                e.printStackTrace();
                connected = false;
            }
            CommandExecutor executor = new CommandExecutor(server);
            executor.setCurrentPath(currentPath);
            while (connected) {
                System.out.print(prompt);
                String input = in.nextLine();
                String[] commands = Parser.parse(input);
                try {
                    currentPath = executor.execute(commands, userName);
                    prompt = currentPath + ">";
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
