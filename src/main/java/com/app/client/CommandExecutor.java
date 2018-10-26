package com.app.client;

import com.app.server.RemoteServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.Arrays;

public class CommandExecutor {

    static final String[] COMMANDS = {"cd", "md", "rd", "deltree", "mf", "del", "block", "unblock", "copy", "move", "print", "dir"};
    private String[] listRoots = {""};

    private String currentPath;
    private RemoteServer server;

    public CommandExecutor(RemoteServer server) {
        this.server = server;
        try {
            listRoots = server.getRoots();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }

    public String execute(String[] commands, String userName) throws RemoteException {
        switch (commands[0].toLowerCase()) {
            case "cd": {
                if (commands.length == 1) {
                    System.out.println(currentPath);
                    return currentPath;
                }
                String newPath = Arrays.stream(commands).skip(1).reduce((x, y) -> x + " " + y).get();
                newPath = combinePaths(currentPath, newPath);
                if (!server.isDirectoryExist(newPath)) {
                    System.out.println("path is not exist");
                    return currentPath;
                } else {
                    setCurrentPath(newPath);
                    return newPath;
                }
            }
            case "md": {
                if (commands.length == 1) {
                    System.out.println("zero arguments");
                    return currentPath;
                }
                if (commands.length > 2) {
                    System.out.println("invalid arguments");
                    return currentPath;
                }
                String path = combinePaths(currentPath, commands[1]);
                server.makeDirectory(path);
                server.addMessage(String.format("directory \"%s\" created by user %s", path, userName));
                return currentPath;
            }
            case "print": {
                System.out.println(server.getFilesTree(currentPath));
                return currentPath;
            }
            case "rd": {
                if (commands.length < 2) {
                    System.out.println("\nwrong arguments");
                    return currentPath;
                }

                String newPath = Arrays.stream(commands).skip(1).reduce((x, y) -> x + " " + y).get();
                newPath = combinePaths(currentPath, newPath);



                if (newPath.equals(currentPath)) {
                    System.out.println("can't delete current directory");
                    return currentPath;
                }

                if (server.isBlocked(Paths.get(newPath))) {
                    System.out.println("can't delete blocked directory");
                    return currentPath;
                }

                boolean removed = server.removeDirectoryRecursive(Paths.get(newPath));
                if (removed) {
                    server.addMessage(String.format("directory \"%s\" deleted by user %s", newPath, userName));
                }
                return currentPath;
            }
            case "dir": {
                if (commands.length != 1) {
                    System.out.println("wrong arguments");
                    return currentPath;
                }

                Arrays.stream(server.getFilesInDirectory(currentPath))
                        .sorted()
                        .forEachOrdered(System.out::println);
                return currentPath;
            }
            case "copy": {

                if (commands.length != 3) {
                    System.out.println("wrong arguments. try to put arguments in \"\"");
                    return currentPath;
                }

                String stringPathFrom = commands[1];
                String stringPathTo = commands[2];

                stringPathFrom = combinePaths(currentPath, stringPathFrom);
                stringPathTo = combinePaths(currentPath, stringPathTo);

                server.copy(stringPathFrom, stringPathTo);
                return currentPath;
            }
            case "block": {
                if (commands.length < 2) {
                    System.out.println("wrong arguments");
                    return currentPath;
                }

                String newPath = Arrays.stream(commands).skip(1).reduce((x, y) -> x + " " + y).get();
                newPath = combinePaths(currentPath, newPath);

                server.block(Paths.get(newPath), userName);
                server.addMessage(String.format("file \"%s\" is blocked by %s", newPath, userName));
                return currentPath;
            }
            case "unblock" : {

                if (commands.length < 2) {
                    System.out.println("wrong arguments");
                    return currentPath;
                }

                String newPath = Arrays.stream(commands).skip(1).reduce((x, y) -> x + " " + y).get();
                newPath = combinePaths(currentPath, newPath);

                server.unblock(Paths.get(newPath), userName);
                server.addMessage(String.format("file \"%s\" is unblocked by %s", newPath, userName));
                return currentPath;
            }
            case "move": {
                if (commands.length != 3) {
                    System.out.println("wrong arguments. try to put arguments in \"\"");
                    return currentPath;
                }

                String stringPathFrom = commands[1];
                String stringPathTo = commands[2];

                stringPathFrom = combinePaths(currentPath, stringPathFrom);
                stringPathTo = combinePaths(currentPath, stringPathTo);

                server.move(stringPathFrom, stringPathTo);
                return currentPath;
            }
            case "deltree": {

                if (commands.length < 2) {
                    System.out.println("\nwrong arguments");
                    return currentPath;
                }

                String newPath = Arrays.stream(commands).skip(1).reduce((x, y) -> x + " " + y).get();
                newPath = combinePaths(currentPath, newPath);

                if (newPath.equals(currentPath)) {
                    System.out.println("can't delete current directory");
                    return currentPath;
                }

                if (server.isBlocked(Paths.get(newPath))) {
                    System.out.println("can't delete blocked directory");
                    return currentPath;
                }

                try {
                    if (Files.walk(Paths.get(newPath)).anyMatch(x -> {
                        try {
                            return server.isBlocked(x);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        return false;
                    })) {
                        System.out.println("directory contains blocked file");
                        return currentPath;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                boolean removed = server.removeDirectoryRecursive(Paths.get(newPath));
                if (removed) {
                    server.addMessage(String.format("directory \"%s\" deleted by user %s", newPath, userName));
                }
                return currentPath;
            }



        }
        return currentPath;
    }

    private String combinePaths(String currentPath, String newPath) {
        String combined = newPath;
        if (Arrays.stream(listRoots).noneMatch(newPath::startsWith)) {
            try {
                combined = currentPath + server.getDelimiter() + newPath;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        combined = Paths.get(combined).normalize().toString();

        return combined;
    }
}
