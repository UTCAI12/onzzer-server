package fr.utc.onzzer.server.communication.events;

import fr.utc.onzzer.common.dataclass.communication.SocketMessage;
import fr.utc.onzzer.server.communication.ServerSocketManager;

import java.io.Serializable;

public record SenderSocketMessage(SocketMessage message, ServerSocketManager sender) implements Serializable {
}
