package fr.utc.onzzer.server;

public class MainServer {

    public static void main(String[] args) {
        final GlobalController globalController = new GlobalController(8000);
        globalController.getComServicesProvider().start();
    }
}
