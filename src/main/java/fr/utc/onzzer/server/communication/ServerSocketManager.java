package fr.utc.onzzer.server.communication;

import fr.utc.onzzer.common.dataclass.communication.SocketMessage;
import fr.utc.onzzer.common.dataclass.communication.SocketMessagesTypes;
import fr.utc.onzzer.common.dataclass.UserLite;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerSocketManager extends Thread {

    private final Socket socket;
    private ObjectOutputStream  out;

    private UserLite user;
    private final ServerCommunicationController serverController;

    public ServerSocketManager(final Socket socket, final ServerCommunicationController serverController) {
        this.serverController = serverController;
        this.socket = socket;

        try {
            this.out = new ObjectOutputStream (socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UserLite getUser() {
        return this.user;
    }

    public void setUser(final UserLite user) {
        this.user = user;
    }

    public void send(final SocketMessage message) throws IOException {
        this.out.writeObject(message);
    }

    @Override
    public void run() {
        try (
                final ObjectInputStream in = new ObjectInputStream(this.socket.getInputStream())
        ) {
            while (true) {
                try {
                    SocketMessage receivedMessage = (SocketMessage) in.readObject();
                    System.out.println("Server: received "+ receivedMessage);

                    this.serverController.onMessage(receivedMessage, this);

                } catch (java.net.SocketException e) {
                    // If user disconnect without sending a DISCONNECT message, create one
                    final SocketMessage socketMessage = new SocketMessage(SocketMessagesTypes.USER_DISCONNECT, this.user);
                    this.serverController.onMessage(socketMessage, this);
                    return;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (EOFException e) {
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void start() {
        System.out.println("Server: New socket");
        super.start();
    }


}
