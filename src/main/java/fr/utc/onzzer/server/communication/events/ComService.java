package fr.utc.onzzer.server.communication.events;

import fr.utc.onzzer.common.dataclass.UpdateListener;

import java.io.Serializable;

public interface ComService {

    public <T extends Serializable> void addNetworkListener(UpdateListener<SenderSocketMessage> listener, SocketMessageDirection direction);

    public <T extends Serializable> void notifyNetworkMessage(SenderSocketMessage senderSocketMessage, SocketMessageDirection direction);
}
