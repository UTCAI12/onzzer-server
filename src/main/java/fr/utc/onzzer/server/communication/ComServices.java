package fr.utc.onzzer.server.communication;

import fr.utc.onzzer.common.dataclass.communication.SocketMessage;
import fr.utc.onzzer.server.communication.impl.ServerSocketManager;

import java.util.UUID;

public interface ComServices
{

    public void onMessage(final SocketMessage message, final ServerSocketManager sender);

    public void start();

}
