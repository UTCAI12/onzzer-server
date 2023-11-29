import java.util.UUID;
import fr.utc.onzzer.common.dataclass.Track;

public interface DataTrackServices {
    void updateTrack(TrackLite track) throws Exception;

    Track getTrack(UUID trackId) throws Exception;

    void removeAllTracks(UUID userUuid) throws Exception;
}
