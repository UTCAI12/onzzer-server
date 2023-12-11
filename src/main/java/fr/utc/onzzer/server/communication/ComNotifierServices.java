package fr.utc.onzzer.server.communication;

import fr.utc.onzzer.common.dataclass.UpdateListener;
import fr.utc.onzzer.server.communication.events.SenderSocketMessage;
import fr.utc.onzzer.server.communication.events.SocketMessageDirection;

import java.io.Serializable;

public interface ComNotifierServices {

    public <T extends Serializable> void addNetworkListener(UpdateListener<SenderSocketMessage> listener, SocketMessageDirection direction);

    public <T extends Serializable> void notifyNetworkMessage(SenderSocketMessage senderSocketMessage, SocketMessageDirection direction);
}
