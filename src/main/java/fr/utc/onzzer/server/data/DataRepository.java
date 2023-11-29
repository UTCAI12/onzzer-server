package fr.utc.onzzer.server.data;

import fr.utc.onzzer.common.dataclass.TrackLite;
import fr.utc.onzzer.common.dataclass.UserLite;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataRepository {
    private final Map<UserLite, List<TrackLite>> usersAndTracks = new HashMap<>();

    public Map<UserLite, List<TrackLite>> getUsersAndTracks() {
        return usersAndTracks;
    }
}
