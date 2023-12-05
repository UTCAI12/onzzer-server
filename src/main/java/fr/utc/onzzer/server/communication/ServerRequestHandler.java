package fr.utc.onzzer.server.communication;


import fr.utc.onzzer.common.dataclass.Track;
import fr.utc.onzzer.common.dataclass.communication.SocketMessage;
import fr.utc.onzzer.common.dataclass.communication.SocketMessagesTypes;
import fr.utc.onzzer.common.dataclass.TrackLite;
import fr.utc.onzzer.common.dataclass.UserLite;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class ServerRequestHandler {

    private final Map<UserLite, ServerSocketManager> users;
    public ServerRequestHandler(final Map<UserLite, ServerSocketManager> users) {
        this.users = users;
    }

    public void sendAllExclude(final SocketMessage message, final UUID excluded) {
        System.out.println("Server: send message to all registered users excluded : " + excluded);
        this.users.forEach((user, serverSocketManager) -> {
            if (excluded != user.getId()) {
                serverSocketManager.send(message);
                System.out.println("Server: sending to:" + user.getId());
            }
        });
    }

    void userConnect(final SocketMessage message, final UserLite userLite, final ServerSocketManager sender) {
        // update the local model with the new user
        this.users.put(userLite, sender);

        // associate the ClientHandler with the appropriate User
        sender.setUser(userLite);

        System.out.println("Server: new client : "+ userLite.getId() + " ! there is now " + this.users.size() + " registered clients");

        // Notify all users exclude "user" in parameter that a new user is connected (just forwarding the initial message)
        this.sendAllExclude(message, userLite.getId());

        // TODO : remove multiple message sending, send only one global message : SocketMessagesTypes.USER_CONNECTED
        SocketMessage m = new SocketMessage(SocketMessagesTypes.USER_CONNECTED, new ArrayList<>(users.keySet()));
        sender.send(m);

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
        this.users.remove(userLite);
        // notifying others users by forwarding the initial message
        this.sendAllExclude(message, userLite.getId());
    }


    void publishTrack(final SocketMessage message, final TrackLite trackLite, final ServerSocketManager sender) {
        // each sender (ClientSocketHandler) has a user associated, forwarding the new track
        this.sendAllExclude(message, sender.getUser().getId());
    }

    void downloadTrack(final SocketMessage message, final Track trackId, final ServerSocketManager sender) {
        //send mesage to the user asking for the track
        //this.sendAllExclude(message, sender.getUser().getId());
    }
}
