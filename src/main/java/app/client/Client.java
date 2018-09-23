package app.client;

import app.server.RemoteServer;

import java.util.Scanner;

public class Client implements Runnable {

    private static final String NO_PATH = "";
    private static final String NO_NAME = "";
    private static final String[] CLIENT_COMMANDS = {"cd",};

    private String currentPath;
    private String prompt;
    private String name;
    private ClientConnector connector;
    private RemoteServer server;
    private boolean connected;

    public Client() {
        currentPath = NO_PATH;
        name = NO_NAME;
    }

    public void run() {


        connector = new ClientConnector();
        Scanner input = new Scanner(System.in);
        while (!connected) {
            String connectionInputMessage = input.nextLine();
            String[] args = connectionInputMessage.split(" ");


            String command = args[0];
            String[] addressPort = args[1].split(":");
            String address = addressPort[0];
            int port = Integer.parseInt(addressPort[1]);
            String userName = args[2];

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

            } catch (Exception e) {
                System.out.println("Connection failed.");
                connected = false;
            }
        }

        String command;
        while (connected) {
            System.out.print(prompt);
            command = input.nextLine();

            executeCommand(command);
        }

    }

    private void executeCommand(String command) {



    }


}
