package fr.utc.onzzer.server;

import fr.utc.onzzer.server.communication.impl.ServerCommunicationController;
import fr.utc.onzzer.server.data.DataServicesProvider;

public class GlobalController {

    private final DataServicesProvider dataServicesProvider;

    private final ServerCommunicationController comServicesProvider;

    public GlobalController(int port) {
        this.dataServicesProvider = new DataServicesProvider();
        this.comServicesProvider = new ServerCommunicationController(port, this.dataServicesProvider);
    }

    public DataServicesProvider getDataServicesProvider() {
        return this.dataServicesProvider;
    }

    public ServerCommunicationController getComServicesProvider() {
        return this.comServicesProvider;
    }
}
