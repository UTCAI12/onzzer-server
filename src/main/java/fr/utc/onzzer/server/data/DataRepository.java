package fr.utc.onzzer.server.data;

import fr.utc.onzzer.common.dataclass.TrackLite;
import fr.utc.onzzer.common.dataclass.UserLite;
import fr.utc.onzzer.server.communication.ServerSocketManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataRepository {
    private final Map<UserLite, List<TrackLite>> usersAndTracks = new HashMap<>();
    private final Map<UserLite, ServerSocketManager> usersAndSocket = new HashMap<>();

    public Map<UserLite, List<TrackLite>> getUsersAndTracks() {
        return usersAndTracks;
    }

    public Map<UserLite, ServerSocketManager> getUsersAndSocket() {
        return usersAndSocket;
    }
}
