package fr.utc.onzzer.server.communication;


import fr.utc.onzzer.common.dataclass.TrackLite;
import fr.utc.onzzer.common.dataclass.UserLite;
import fr.utc.onzzer.common.dataclass.communication.SocketMessage;
import fr.utc.onzzer.common.dataclass.communication.SocketMessagesTypes;
import fr.utc.onzzer.server.communication.events.Notifier;
import fr.utc.onzzer.server.communication.events.SenderSocketMessage;
import fr.utc.onzzer.server.communication.events.SocketMessageDirection;
import fr.utc.onzzer.server.data.ServerController;
import fr.utc.onzzer.server.data.exceptions.TrackLiteNotFoundException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class ServerCommunicationController extends Notifier {
    private final int serverPort;

    private final Map<SocketMessagesTypes, BiConsumer<SocketMessage, ServerSocketManager>> messageHandlers;

    private final ServerRequestHandler serverRequestHandler;

    private final ServerController serverController;

    public ServerCommunicationController(final int serverPort, ServerController serverController) {
        this.serverPort = serverPort;
        this.serverController = serverController;
        this.serverRequestHandler = new ServerRequestHandler(serverController);

        this.messageHandlers = new HashMap<>();
        // Associez les types de message aux méthodes correspondantes de clientHandler
        messageHandlers.put(SocketMessagesTypes.USER_CONNECT, (message, sender) -> {
            serverRequestHandler.userConnect(message, (UserLite) message.object, sender);
        });
        messageHandlers.put(SocketMessagesTypes.USER_DISCONNECT, (message, sender) -> {
            serverRequestHandler.userDisconnect(message, (UserLite) message.object, sender);
        });
        messageHandlers.put(SocketMessagesTypes.UPDATE_TRACK, (message, sender) -> {
            try {
                serverRequestHandler.updateTrack(message, (TrackLite) message.object, sender);
            } catch (TrackLiteNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        messageHandlers.put(SocketMessagesTypes.PUBLISH_TRACK, (message, sender) -> {
            try {
                serverRequestHandler.publishTrack(message, (TrackLite) message.object, sender);
            } catch (TrackLiteNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        messageHandlers.put(SocketMessagesTypes.UNPUBLISH_TRACK, (message, sender) -> {
            try {
                serverRequestHandler.unpublishTrack(message, (TrackLite) message.object, sender);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        messageHandlers.put(SocketMessagesTypes.USER_PING, (message, sender) -> {
            // No action required after user ping
        });
        messageHandlers.put(SocketMessagesTypes.GET_TRACK, (message, sender) -> {
            serverRequestHandler.handleGetTrack(message, sender);
        });
        messageHandlers.put(SocketMessagesTypes.DOWNLOAD_TRACK, (message, sender) -> {
            serverRequestHandler.downloadTrack(message, sender);
        });
    }

    /**
     * This method is called by the ClientHandler (instance how receives messages from the socket of a specific client
     * @param message The message send by the client
     * @param sender The Socket how sent the message
     */
    public void onMessage(final SocketMessage message, final ServerSocketManager sender) {
        // getting the method associated to the message type
        BiConsumer<SocketMessage, ServerSocketManager> handler = messageHandlers.get(message.messageType);

        if (handler != null) {
            // if handler is not null, means that a method is defined
            handler.accept(message, sender);
        } else {
            // if handler is null, no function for this message type
            System.err.println("Unhandled message");
        }

        final SenderSocketMessage senderSocketMessage = new SenderSocketMessage(message, sender);
        this.notifyNetworkMessage(senderSocketMessage, SocketMessageDirection.IN);
    }

    public void start() {
        new Thread(() -> startServer()).start();
    }

    private void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(this.serverPort)) {
            System.out.println("Server: Server started on port " + this.serverPort);
            while (true) {
                final Socket clientSocket = serverSocket.accept();
                new ServerSocketManager(clientSocket, this).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}