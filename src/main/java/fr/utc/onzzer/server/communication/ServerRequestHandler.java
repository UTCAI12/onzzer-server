package fr.utc.onzzer.server.communication;

import fr.utc.onzzer.common.dataclass.User;
import fr.utc.onzzer.server.data.ServerController;
import fr.utc.onzzer.common.dataclass.communication.SocketMessage;
import fr.utc.onzzer.common.dataclass.communication.SocketMessagesTypes;
import fr.utc.onzzer.common.dataclass.TrackLite;
import fr.utc.onzzer.common.dataclass.UserLite;
import fr.utc.onzzer.server.data.exceptions.TrackLiteNotFoundException;
import fr.utc.onzzer.server.data.exceptions.UserLiteNotFoundException;
import fr.utc.onzzer.server.data.interfaces.DataTrackServices;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;


public class ServerRequestHandler {

    private ServerController serverController;

    public ServerRequestHandler(ServerController serverController) {
        this.serverController = serverController;
    }

    public void sendAllExclude(final SocketMessage message, final UUID excluded) {
        System.out.println("Server: send message to all registered users excluded : " + excluded);

        for (UserLite user: this.serverController.getDataUserServices().getAllUsers()) {
            try {
                if (excluded != user.getId()) {
                    try {
                        this.serverController.getDataUserServices().getSocket(user).send(message);
                    } catch (IOException e) {
                        System.out.println("Server: Not able to reach user, removing it from list");
                        this.serverController.getDataUserServices().deleteUser(user);
                    }
                    System.out.println("Server: sending to:" + user.getId());
                }
            } catch (UserLiteNotFoundException e) {
                System.out.println("Server: The user does not exists");
            }
        }
    }

    public void sendMessageToUser(final SocketMessage message, final UserLite user) {
        try {
            ServerSocketManager userSocketManager = this.serverController.getDataUserServices().getSocket(user);
            if (userSocketManager != null) {
                try {
                    userSocketManager.send(message);
                    System.out.println("Server: GET_TRACK message sent to user: " + user.getUsername());
                } catch (IOException e) {
                    System.out.println("Server: Not able to reach user " + user.getUsername());
                    this.serverController.getDataUserServices().deleteUser(user); // Remove the user if we can't reach them
                }
            } else {
                System.out.println("Server: User with ID " + user.getUsername() + " not found.");
            }
        } catch (UserLiteNotFoundException e) {
            System.out.println("Server: The user does not exist");
        }
    }


    void userConnect(final SocketMessage message, final UserLite userLite, final ServerSocketManager sender) {
        // update the local model with the new user
        this.serverController.getDataUserServices().addUser(userLite, sender);

        // associate the ClientHandler with the appropriate User
        sender.setUser(userLite);

        System.out.println("Server: new client : "+ userLite.getId() + " ! there is now " + this.serverController.getDataUserServices().getAllUsers().size() + " registered clients");

        // Notify all users exclude "user" in parameter that a new user is connected (just forwarding the initial message)
        this.sendAllExclude(message, userLite.getId());

        // TODO : remove multiple message sending, send only one global message : SocketMessagesTypes.USER_CONNECTED
        SocketMessage m = new SocketMessage(SocketMessagesTypes.USER_CONNECTED, new ArrayList<>(this.serverController.getDataUserServices().getAllUsers()));
        try {
            sender.send(m);
        } catch (IOException e) {
            try {
                this.serverController.getDataUserServices().deleteUser(userLite);
            } catch (UserLiteNotFoundException e2){
                System.out.println("Server: The user does not exist");
            }
        }

        /*
        this.users.forEach((registeredUser, handler) -> {
            System.out.println("Server: Notifying this new client with all current clients");
            if (registeredUser.getUuid() != userLite.getUuid()) {
                SocketMessage m = new SocketMessage(SocketMessagesTypes.USER_CONNECT, registeredUser);
                System.out.println("Server: sending user: " + registeredUser.getUuid());
            }
        });
        */
    }

    void userDisconnect(final SocketMessage message, final UserLite userLite, final ServerSocketManager sender) {
        // removing the user from the local "model"
        try {
            this.serverController.getDataUserServices().deleteUser(userLite);
        } catch (UserLiteNotFoundException e) {
            System.out.println("Server: The user does not exist");
        }

        // notifying others users by forwarding the initial message
        this.sendAllExclude(message, userLite.getId());
    }


    void publishTrack(final SocketMessage message, final TrackLite trackLite, final ServerSocketManager sender) {
        // each sender (ClientSocketHandler) has a user associated, forwarding the new track
        this.sendAllExclude(message, sender.getUser().getId());
    }

    void handleGetTrack(final SocketMessage message, final ServerSocketManager sender) {

        class TrackRequest implements Serializable {
            private final UUID trackId;
            private final UUID userId;

            public TrackRequest(UUID trackId, UUID userId) {
                this.trackId = trackId;
                this.userId = userId;
            }

            public UUID getTrackId() {
                return trackId;
            }

            public UUID getUserId() {
                return userId;
            }
        }

        try {
            UUID trackId = (UUID) message.object;
            UserLite owner = serverController.getDataTrackServices().getOwner(trackId);
            TrackRequest trackRequest = new TrackRequest(trackId, sender.getUser().getId());
            sendMessageToUser(new SocketMessage(SocketMessagesTypes.GET_TRACK, trackRequest), owner);
        } catch (UserLiteNotFoundException e) {
            System.out.println("User not found: " + e.getMessage());
        } catch (TrackLiteNotFoundException e) {
            System.out.println("Track not found: " + e.getMessage());
        }

    }
}
