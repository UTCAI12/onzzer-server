package fr.utc.onzzer.server;

import fr.utc.onzzer.server.communication.ServerCommunicationController;
import fr.utc.onzzer.server.data.ServerController;


public class MainServer {

    public static void main(String[] args) {
        final GlobalController globalController = new GlobalController(8000);
        globalController.getComServicesProvider().start();
    }
}
