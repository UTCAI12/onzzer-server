package fr.utc.onzzer.server.data.interfaces;

import java.util.UUID;
import fr.utc.onzzer.common.dataclass.Track;
import fr.utc.onzzer.common.dataclass.TrackLite;

public interface DataTrackServices {
    void updateTrack(TrackLite track) throws Exception;

    Track getTrack(UUID trackId) throws Exception;

    void removeAllTracks(UUID userUuid) throws Exception;
}
