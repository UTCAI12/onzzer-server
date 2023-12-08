package fr.utc.onzzer.server;

import fr.utc.onzzer.server.communication.ServerCommunicationController;
import fr.utc.onzzer.server.data.ServerController;

public class GlobalController {

    private final ServerController dataServicesProvider;

    private final ServerCommunicationController comServicesProvider;

    public GlobalController(int port) {
        this.dataServicesProvider = new ServerController();
        this.comServicesProvider = new ServerCommunicationController(port, this.dataServicesProvider);
    }

    public ServerController getDataServicesProvider() {
        return this.dataServicesProvider;
    }

    public ServerCommunicationController getComServicesProvider() {
        return this.comServicesProvider;
    }
}
