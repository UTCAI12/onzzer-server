package fr.utc.onzzer.server;

public class MainServer {

    public static void main(String[] args) {
        final GlobalController globalController = new GlobalController(8001);
        globalController.getComServicesProvider().start();
    }
}
