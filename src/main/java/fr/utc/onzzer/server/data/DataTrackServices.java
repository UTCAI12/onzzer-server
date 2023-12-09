package fr.utc.onzzer.server.data;

import java.util.UUID;
import java.util.List;
import fr.utc.onzzer.common.dataclass.Track;
import fr.utc.onzzer.common.dataclass.TrackLite;
import fr.utc.onzzer.common.dataclass.UserLite;
import fr.utc.onzzer.common.services.Service;
import fr.utc.onzzer.server.data.exceptions.RequestedTrackNotFound;
import fr.utc.onzzer.server.data.exceptions.TrackLiteNotFoundException;
import fr.utc.onzzer.server.data.exceptions.UserLiteNotFoundException;

public interface DataTrackServices extends Service {

    void addTrack(TrackLite track, UserLite user);
    
    void updateTrack(TrackLite track) throws TrackLiteNotFoundException;

    TrackLite getTrack(UUID trackId) throws TrackLiteNotFoundException;

    UserLite getOwner(UUID trackId) throws UserLiteNotFoundException, TrackLiteNotFoundException;

    void removeTrack(TrackLite track, UserLite user) throws Exception;

    void removeAllTracks(UserLite userLite) throws TrackLiteNotFoundException;

    List<TrackLite> getAllTracks();

    // Link between DownloadedTrack and UserLite(s)
    void addRequestedTrackForUser(UUID trackId, UserLite user);

    void removeRequestedTrackForUser(UUID trackId, UserLite user) throws RequestedTrackNotFound;

    //return List of Users by trackId
    List<UserLite> getUsersForRequestedTrack(UUID trackId) throws RequestedTrackNotFound;
}
