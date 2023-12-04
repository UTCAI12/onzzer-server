package fr.utc.onzzer.server;

import fr.utc.onzzer.server.communication.ServerCommunicationController;
import fr.utc.onzzer.server.data.ServerController;


public class MainServer {

    public static void main(String[] args) {
        final ServerController serverController = new ServerController();

        ServerCommunicationController server = new ServerCommunicationController(8000, serverController);
        server.start();
    }
}
