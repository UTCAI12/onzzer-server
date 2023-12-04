package fr.utc.onzzer.server.data.interfaces;

import java.util.UUID;
import fr.utc.onzzer.common.dataclass.Track;
import fr.utc.onzzer.common.dataclass.TrackLite;
import fr.utc.onzzer.common.dataclass.UserLite;
import fr.utc.onzzer.server.data.exceptions.TrackLiteNotFoundException;
import fr.utc.onzzer.server.data.exceptions.UserLiteNotFoundException;

public interface DataTrackServices {
    void updateTrack(TrackLite track) throws TrackLiteNotFoundException;

    TrackLite getTrack(UUID trackId) throws TrackLiteNotFoundException;

    UserLite getOwner(UUID trackId) throws UserLiteNotFoundException, TrackLiteNotFoundException;

    void removeAllTracks(UserLite userLite) throws TrackLiteNotFoundException;
}
