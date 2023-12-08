package fr.utc.onzzer.server;

import fr.utc.onzzer.server.communication.ServerCommunicationController;
import fr.utc.onzzer.server.communication.events.SocketMessageDirection;
import fr.utc.onzzer.server.data.ServerController;


public class MainServer {

    public static void main(String[] args) {
        final ServerController serverController = new ServerController();

        ServerCommunicationController server = new ServerCommunicationController(8000, serverController);

        server.addNetworkListener((senderSocketMessage -> {
            System.out.println("Server network event : Message received from " + senderSocketMessage.sender().getUser().getUsername() +
                    ":" + senderSocketMessage.message().messageType);
        }), SocketMessageDirection.IN);

        server.addNetworkListener((senderSocketMessage -> {
            System.out.println("Server network event : Message sent to " + senderSocketMessage.sender().getUser().getUsername() +
                    ":" + senderSocketMessage.message().messageType);
        }), SocketMessageDirection.OUT);

        server.start();
    }
}
