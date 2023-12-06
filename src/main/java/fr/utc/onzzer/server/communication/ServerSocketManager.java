package fr.utc.onzzer.server.communication;

import fr.utc.onzzer.common.dataclass.communication.SocketMessage;
import fr.utc.onzzer.common.dataclass.communication.SocketMessagesTypes;
import fr.utc.onzzer.common.dataclass.UserLite;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Timer;
import java.util.TimerTask;

public class ServerSocketManager extends Thread {

    private final Socket socket;
    private ObjectOutputStream  out;
    private ObjectInputStream in;

    private UserLite user;
    private final ServerCommunicationController serverController;
    private static final long PING_INTERVAL = 1000; // 1 seconds
    private static final int TIMEOUT = 2000; // 2 seconds

    public ServerSocketManager(final Socket socket, final ServerCommunicationController serverController) {
        this.serverController = serverController;
        this.socket = socket;

        try {
            this.out = new ObjectOutputStream (socket.getOutputStream());
            this.in = new ObjectInputStream (socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        /* Create a thread for pinging continuously the client */
        new Thread(() -> pingClient(socket)).start();
    }

    public UserLite getUser() {
        return this.user;
    }

    public void setUser(final UserLite user) {
        this.user = user;
    }

    public void send(final SocketMessage message) {
        try {
            this.out.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pingClient(Socket clientSocket) {
        try {
            // Send ping messages at regular intervals
            Timer timerPong = new Timer();
            timerPong.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    send(SocketMessagesTypes.SERVER_PING);
                }
            }, 0, PING_INTERVAL);

            clientSocket.setSoTimeout(TIMEOUT);

            // Listen for responses from the client
            while (!clientSocket.isClosed()) {
                try {
                    Object message = in.readObject();
                    if (message == null) {
                        System.out.println("Client disconnected: " + clientSocket.getInetAddress().getHostAddress());
                        clientSocket.close();
                    }
                } catch (SocketTimeoutException e) {
                    System.out.println("Client TIMEOUT: " + clientSocket.getInetAddress().getHostAddress());
                    clientSocket.close();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
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
