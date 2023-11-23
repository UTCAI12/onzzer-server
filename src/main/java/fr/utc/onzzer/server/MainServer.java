package fr.utc.onzzer.server;

import fr.utc.onzzer.server.communication.ServerCommunicationController;

public class MainServer {

    public static void main(String[] args) {
        ServerCommunicationController server = new ServerCommunicationController(8000);
        server.start();
    }
}
