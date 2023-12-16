package fr.utc.onzzer.server.data;

import fr.utc.onzzer.common.dataclass.TrackLite;
import fr.utc.onzzer.common.dataclass.UserLite;
import fr.utc.onzzer.server.communication.impl.ServerSocketManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DataRepository {
    private final Map<UserLite, List<TrackLite>> usersAndTracks = new HashMap<>();
    private final Map<UserLite, ServerSocketManager> usersAndSockets = new HashMap<>();

    // Link DownloadedTrack to right UserLite(s)
    // Allow com to send TrackLite to right UserLite(s) when a Track is fully downloaded
    private final Map<UUID /*trackId*/, List<UserLite>> RequestedTracksForUsers = new HashMap<>();

    public Map<UserLite, List<TrackLite>> getUsersAndTracks() {
        return usersAndTracks;
    }

    public Map<UserLite, ServerSocketManager> getUsersAndSockets() {
        return usersAndSockets;
    }

    public Map<UUID , List<UserLite>> getRequestedTracksForUsers() {
        return RequestedTracksForUsers;
    }
}
