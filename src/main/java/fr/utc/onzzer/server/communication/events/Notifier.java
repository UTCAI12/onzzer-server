package fr.utc.onzzer.server.communication.events;

import fr.utc.onzzer.common.dataclass.UpdateListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Notifier implements ComService {

    private final List<ListenerCom> listeners = new ArrayList<>();

    public Notifier() {

    }

    public <T extends Serializable> void addNetworkListener(UpdateListener<SenderSocketMessage> listener, SocketMessageDirection direction) {
        this.listeners.add(new ListenerCom(listener, direction));
    }

    public <T extends Serializable> void notifyNetworkMessage(SenderSocketMessage senderSocketMessage, SocketMessageDirection direction) {
        this.listeners.stream()
                .filter(listener -> listener.direction.equals(direction))
                .map(listener -> listener.listener)
                .forEach(listener -> listener.onUpdate(senderSocketMessage));
    }

    record ListenerCom(UpdateListener<SenderSocketMessage> listener, SocketMessageDirection direction) {
    }
}