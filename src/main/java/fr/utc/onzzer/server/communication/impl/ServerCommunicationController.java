package fr.utc.onzzer.server.communication.impl;

import fr.utc.onzzer.common.dataclass.TrackLite;
import fr.utc.onzzer.common.dataclass.UserLite;
import fr.utc.onzzer.common.dataclass.communication.SocketMessage;
import fr.utc.onzzer.common.dataclass.communication.SocketMessagesTypes;
import fr.utc.onzzer.server.communication.ComServices;
import fr.utc.onzzer.server.communication.ComNotifierServices;
import fr.utc.onzzer.server.communication.events.Notifier;
import fr.utc.onzzer.server.communication.events.SenderSocketMessage;
import fr.utc.onzzer.server.communication.events.SocketMessageDirection;
import fr.utc.onzzer.server.data.DataServicesProvider;
import fr.utc.onzzer.server.data.exceptions.TrackLiteNotFoundException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class ServerCommunicationController extends Notifier implements ComServices, ComNotifierServices {
    private final int serverPort;

    private final Map<SocketMessagesTypes, BiConsumer<SocketMessage, ServerSocketManager>> messageHandlers;

    private final ServerRequestHandler serverRequestHandler;

    private final DataServicesProvider dataServicesProvider;

    public ServerCommunicationController(final int serverPort, DataServicesProvider dataServicesProvider) {
        this.serverPort = serverPort;
        this.dataServicesProvider = dataServicesProvider;
        this.serverRequestHandler = new ServerRequestHandler(dataServicesProvider);
        this.messageHandlers = new HashMap<>();
        this.handleMessages();
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
        new Thread(this::startServer).start();
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

    public void handleMessages(){

        // Handle USER_CONNECT
        this.messageHandlers.put(SocketMessagesTypes.USER_CONNECT, (message, sender) -> {
            serverRequestHandler.userConnect(message, (UserLite) message.object, sender);
        });
        // Handle USER_DISCONNECT
        this.messageHandlers.put(SocketMessagesTypes.USER_DISCONNECT, (message, sender) -> {
            serverRequestHandler.userDisconnect(message, (UserLite) message.object, sender);
        });
        // Handle UPDATE_TRACK
        messageHandlers.put(SocketMessagesTypes.UPDATE_TRACK, (message, sender) -> {
            try {
                serverRequestHandler.updateTrack(message, (TrackLite) message.object, sender);
            } catch (TrackLiteNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        // Handle PUBLISH_TRACK
        messageHandlers.put(SocketMessagesTypes.PUBLISH_TRACK, (message, sender) -> {
            try {
                serverRequestHandler.publishTrack(message, (TrackLite) message.object, sender);
            } catch (TrackLiteNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
        // Handle UNPUBLISH_TRACK
        messageHandlers.put(SocketMessagesTypes.UNPUBLISH_TRACK, (message, sender) -> {
            try {
                serverRequestHandler.unpublishTrack(message, (TrackLite) message.object, sender);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        // Handle PUBLISH_RATING
        messageHandlers.put(SocketMessagesTypes.PUBLISH_RATING, (message, sender) -> {
            serverRequestHandler.publishRating(message, (ArrayList<Object>) message.object, sender);
        });
        // Handle USER_PING
        messageHandlers.put(SocketMessagesTypes.USER_PING, (message, sender) -> {
            // No action required after user ping
        });
        // Handle GET_TRACK
        messageHandlers.put(SocketMessagesTypes.GET_TRACK, (message, sender) -> {
            serverRequestHandler.handleGetTrack(message, sender);
        });
        // Handle DOWNLOAD_TRACK
        messageHandlers.put(SocketMessagesTypes.DOWNLOAD_TRACK, (message, sender) -> {
            serverRequestHandler.downloadTrack(message, sender);
        });
        // Handle PUBLISH_COMMENT
        messageHandlers.put(SocketMessagesTypes.PUBLISH_COMMENT, (message, sender) -> {
            serverRequestHandler.publishComment(message, (ArrayList<Object>) message.object, sender);
        });
    }
}