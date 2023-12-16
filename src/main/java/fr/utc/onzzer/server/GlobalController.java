package fr.utc.onzzer.server;

import fr.utc.onzzer.server.communication.ComServices;
import fr.utc.onzzer.server.communication.ComServicesProvider;
import fr.utc.onzzer.server.communication.impl.ServerCommunicationController;
import fr.utc.onzzer.server.data.DataServicesProvider;

public class GlobalController {

    private final DataServicesProvider dataServicesProvider;

    private final ComServicesProvider comServicesProvider;
    public GlobalController(int port) {
        this.dataServicesProvider = new DataServicesProvider();
        this.comServicesProvider = new ComServicesProvider(port, this.dataServicesProvider);
    }

    public DataServicesProvider getDataServicesProvider() {
        return this.dataServicesProvider;
    }


    public ComServicesProvider getComServicesProvider() {return this.comServicesProvider;}
}
