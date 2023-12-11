package fr.utc.onzzer.server.communication;

import fr.utc.onzzer.server.communication.events.Notifier;
import fr.utc.onzzer.server.communication.impl.ServerCommunicationController;
import fr.utc.onzzer.server.data.DataServicesProvider;

public class ComServicesProvider {

    private final ComServices comServices;
    private final ComNotifierServices comNotifierServices;

    public ComServicesProvider(final int serverPort, DataServicesProvider dataServicesProvider) {
        final ComNotifierServices notifier = new Notifier();
        final ServerCommunicationController serverCommunicationController = new ServerCommunicationController(serverPort, dataServicesProvider);

        this.comServices = serverCommunicationController;
        this.comNotifierServices = notifier;
    }

    public ComNotifierServices getComNotifierServices() {
        return comNotifierServices;
    }
    public ComServices getComServices() {
        return comServices;
    }

}
