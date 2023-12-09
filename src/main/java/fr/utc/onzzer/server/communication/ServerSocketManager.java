package fr.utc.onzzer.server.communication;

import fr.utc.onzzer.common.dataclass.communication.SocketMessage;
import fr.utc.onzzer.common.dataclass.communication.SocketMessagesTypes;
import fr.utc.onzzer.common.dataclass.UserLite;
import fr.utc.onzzer.server.communication.events.SenderSocketMessage;
import fr.utc.onzzer.server.communication.events.SocketMessageDirection;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Timer;
import java.util.TimerTask;

public class ServerSocketManager extends Thread {

    private final Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    private UserLite user;
    private final ServerCommunicationController serverController;
    private static final long PING_INTERVAL = 5000; // 5 second
    private static final int TIMEOUT = 10000; // 10 seconds
    private Timer timerPong;

    public ServerSocketManager(final Socket socket, final ServerCommunicationController serverController) {
        this.serverController = serverController;
        this.socket = socket;

        try {
            this.outputStream = new ObjectOutputStream(socket.getOutputStream());
            this.inputStream = new ObjectInputStream(socket.getInputStream());
            this.timerPong = new Timer();
            // Timeout if no data arrives within TIMEOUT value
            socket.setSoTimeout(TIMEOUT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create a timer for pinging continuously the client
        this.timerPong.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    // Check if the socket is open before sending a ping
                    if (!socket.isClosed() && socket.isConnected()) {
                        send(new SocketMessage(SocketMessagesTypes.SERVER_PING, null));
                    } else {
                        this.cancel();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 5000, PING_INTERVAL);

    }

    public UserLite getUser() {
        return this.user;
    }

    public void setUser(final UserLite user) {
        this.user = user;
    }


    public void send(final SocketMessage message) throws IOException {
        this.serverController.notifyNetworkMessage(new SenderSocketMessage(message, this), SocketMessageDirection.OUT);
        this.outputStream.writeObject(message);
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            try {
                SocketMessage receivedMessage = (SocketMessage) inputStream.readObject();
                System.out.println("Server: received " + receivedMessage);
                this.serverController.onMessage(receivedMessage, this);
            } catch (SocketTimeoutException e) {
                System.out.println("Client timeout: " + socket.getInetAddress().getHostAddress());
                break;  // Exit the loop on timeout
            }catch (SocketException | EOFException e) {
                // Exception throw when waiting of an object then the client disconnect
                System.out.println("Client disconnected: " + socket.getInetAddress().getHostAddress());
                break;  // Exit the loop on disconnection
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                break;
            }
        }
        this.disconnect();
    }

    public void disconnect(){
        this.timerPong.cancel();
        final SocketMessage socketMessage = new SocketMessage(SocketMessagesTypes.USER_DISCONNECT, this.user);
        this.serverController.onMessage(socketMessage, this);
        try {
            System.out.println("Server: Socket closed");
            this.socket.close();
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
