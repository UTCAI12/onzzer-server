package fr.utc.onzzer.server.communication;

import fr.utc.onzzer.common.dataclass.*;
import fr.utc.onzzer.common.dataclass.communication.SocketMessage;
import fr.utc.onzzer.common.dataclass.communication.SocketMessagesTypes;
import fr.utc.onzzer.server.data.DataServicesProvider;
import fr.utc.onzzer.server.data.exceptions.RequestedTrackNotFound;
import fr.utc.onzzer.server.data.exceptions.TrackLiteNotFoundException;
import fr.utc.onzzer.server.data.exceptions.UserLiteNotFoundException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


public class ServerRequestHandler {

    private DataServicesProvider serverController;

    public ServerRequestHandler(DataServicesProvider serverController) {
        this.serverController = serverController;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> this.sendAllExclude(new SocketMessage(SocketMessagesTypes.SERVER_STOPPED, null), null)));
    }

    public void sendAllExclude(final SocketMessage message, final UUID excluded) {
        for (UserLite user: this.serverController.getDataUserServices().getAllUsers()) {
            try {
                if (excluded != user.getId()) {
                    try {
                        this.serverController.getDataUserServices().getSocket(user).send(message);
                    } catch (IOException e) {
                        this.serverController.getDataUserServices().deleteUser(user);
                    }
                }
            } catch (UserLiteNotFoundException e) {
                System.err.println("Server: The user does not exists");
            }
        }
    }

    public void sendMessageToUser(final SocketMessage message, final UserLite user) {
        try {
            ServerSocketManager userSocketManager = this.serverController.getDataUserServices().getSocket(user);
            if (userSocketManager != null) {
                try {
                    userSocketManager.send(message);
                } catch (IOException e) {
                    this.serverController.getDataUserServices().deleteUser(user); // Remove the user if we can't reach them
                }
            } else {
                System.err.println("Server: User with ID " + user.getUsername() + " not found.");
            }
        } catch (UserLiteNotFoundException e) {
            System.err.println("Server: The user does not exist");
        }
    }


    void userConnect(final SocketMessage message, final HashMap<UserLite, List<TrackLite>> connectData, final ServerSocketManager sender) {
        UserLite userLite = connectData.keySet().iterator().next();
        List<TrackLite> trackList = connectData.get(userLite);
        try {
            // checks if the user isn't already connected
            for(UserLite user: serverController.getDataUserServices().getAllUsers()) {
                if (user.getId().equals(userLite.getId())) {
                    SocketMessage m = new SocketMessage(SocketMessagesTypes.USER_LOGIN_ERROR, "User already connected");
                    sender.send(m);
                    return;
                }
            }

            SocketMessage m = new SocketMessage(SocketMessagesTypes.USER_CONNECTED, new ArrayList<>(this.serverController.getDataUserServices().getAllUsers()));

            // associate the ClientHandler with the appropriate User
            sender.setUser(userLite);

            sender.send(m);

            // update the local model with the new user
            this.serverController.getDataUserServices().addUser(userLite, sender);
            for(TrackLite t: trackList){
                this.serverController.getDataTrackServices().addTrack(t, userLite);
            }

            // Notify all users exclude "user" in parameter that a new user is connected (just forwarding the initial message)
            this.sendAllExclude(message, userLite.getId());

        } catch (IOException e) {
            System.err.println("Server: an error occurred while sending the message: " + e.getMessage());
        }
    }

    void userDisconnect(final SocketMessage message, final UserLite userLite, final ServerSocketManager sender) {
        sender.close();

        try {
            // removing the user from the local "model"
            this.serverController.getDataUserServices().deleteUser(userLite);
        } catch (UserLiteNotFoundException e) {
            System.err.println("Server: The user does not exist");
        }

        // notifying others users by forwarding the initial message
        this.sendAllExclude(message, userLite.getId());
    }


    void publishTrack(final SocketMessage message, final TrackLite trackLite, final ServerSocketManager sender) throws TrackLiteNotFoundException {
        serverController.getDataTrackServices().addTrack(trackLite, sender.getUser());
        // each sender (ClientSocketHandler) has a user associated, forwarding the new track
        this.sendAllExclude(new SocketMessage(SocketMessagesTypes.PUBLISH_TRACK, trackLite), sender.getUser().getId());
    }

    void publishRating(final SocketMessage message, final ArrayList<Object> rating, final ServerSocketManager sender) {
        try {
            this.serverController.getDataTrackServices().getTrack((UUID) rating.get(0));
            this.sendAllExclude(message, ((Rating) rating.get(1)).getUser().getId());
        } catch (TrackLiteNotFoundException e) {
            System.err.println("Server: the specified track (" + (UUID) rating.get(0) + ") does not exist");
        }
    }

    void updateTrack(final SocketMessage message, final TrackLite trackLite, final ServerSocketManager sender) throws TrackLiteNotFoundException {
        serverController.getDataTrackServices().updateTrack(trackLite);
        // each sender (ClientSocketHandler) has a user associated, forwarding the new track
        this.sendAllExclude(new SocketMessage(SocketMessagesTypes.UPDATE_TRACK, trackLite), sender.getUser().getId());
    }

    void unpublishTrack(final SocketMessage message, final TrackLite trackLite, final ServerSocketManager sender) throws Exception {
        serverController.getDataTrackServices().removeTrack(trackLite, sender.getUser());
        // each sender (ClientSocketHandler) has a user associated, forwarding the new track
        this.sendAllExclude(new SocketMessage(SocketMessagesTypes.UNPUBLISH_TRACK, trackLite), sender.getUser().getId());
    }

    void handleGetTrack(final SocketMessage message, final ServerSocketManager sender) {
        try {
            UUID trackId = (UUID) message.object;
            UserLite owner = serverController.getDataTrackServices().getOwner(trackId);
            serverController.getDataTrackServices().addRequestedTrackForUser(trackId, sender.getUser());
            sendMessageToUser(new SocketMessage(SocketMessagesTypes.GET_TRACK, trackId), owner);
        } catch (UserLiteNotFoundException e) {
            System.err.println("User not found: " + e.getMessage());
        } catch (TrackLiteNotFoundException e) {
            System.err.println("Track not found: " + e.getMessage());
        }

    }

    void downloadTrack(final SocketMessage message, final ServerSocketManager sender) {
        Track track = (Track) message.object;
        try {
            List<UserLite> userList = serverController.getDataTrackServices().getUsersForRequestedTrack(track.getId());
            for(UserLite user : userList) {
                sendMessageToUser(new SocketMessage(SocketMessagesTypes.DOWNLOAD_TRACK, track), user);
                serverController.getDataTrackServices().removeRequestedTrackForUser(track.getId(), user);
            }
        } catch (RequestedTrackNotFound e) {
            System.err.println("Track not found: " + e.getMessage());
        }
    }

    void publishComment(final SocketMessage message, final ArrayList<Object> comment, final ServerSocketManager sender) {
        try {
            this.serverController.getDataTrackServices().getTrack((UUID) comment.get(0));
            this.sendAllExclude(message, ((Comment) comment.get(1)).getUser().getId());
        } catch (TrackLiteNotFoundException e) {
            System.err.println("Server: the specified track (" + (UUID) comment.get(0) + ") does not exist");
        }
    }

    void editUser(final SocketMessage message, final UserLite userLite, final ServerSocketManager sender) {
        try {
            this.serverController.getDataUserServices().updateUser(userLite);
            this.sendAllExclude(message, userLite.getId());
        } catch (UserLiteNotFoundException e) {
            System.err.println("Server: the specified user (" + userLite.getId() + ") does not exist");
        }
    }
}
