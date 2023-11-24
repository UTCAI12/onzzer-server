import java.util.UUID;

public interface DataTrackServices {
    void updateTrack(TrackLite track) throws Exception;

    Track getTrack(UUID trackId) throws Exception;

    void removeAllTracks(UUID userUuid) throws Exception;
}
