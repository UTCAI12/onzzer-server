package fr.utc.onzzer.server.data.impl;

import fr.utc.onzzer.common.dataclass.*;
import fr.utc.onzzer.common.services.Listenable;
import fr.utc.onzzer.server.data.DataRepository;
import fr.utc.onzzer.server.data.exceptions.RequestedTrackNotFound;
import fr.utc.onzzer.server.data.exceptions.TrackLiteNotFoundException;
import fr.utc.onzzer.server.data.exceptions.UserLiteNotFoundException;
import fr.utc.onzzer.server.data.DataTrackServices;

import java.util.*;

public class DataTrackServicesImpl extends Listenable implements DataTrackServices {
    private final DataRepository dataRepository;

    public DataTrackServicesImpl(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @Override
    public void addTrack(TrackLite track, UserLite user) {

        if (!dataRepository.getUsersAndTracks().containsKey(user)) {
            dataRepository.getUsersAndTracks().put(user, new ArrayList<>());
        }
        else if (dataRepository.getUsersAndTracks().get(user).contains(track)) {
            this.notify(track, TrackLite.class, ModelUpdateTypes.UPDATE_TRACK);
            return;
        }
        dataRepository.getUsersAndTracks().get(user).add(track);
    }
    
    @Override
    public void updateTrack(TrackLite newTrack) throws TrackLiteNotFoundException {
        Collection<List<TrackLite>> values = dataRepository.getUsersAndTracks().values();
        for (List<TrackLite> userTrackLites : values) {
            for (TrackLite trackLite: userTrackLites) {
                if (trackLite.getId() == newTrack.getId()) {
                    userTrackLites.add(newTrack);
                    userTrackLites.remove(trackLite);
                    this.notify(newTrack, TrackLite.class, ModelUpdateTypes.UPDATE_TRACK);
                    return;
                }
            }
        }
        throw new TrackLiteNotFoundException();
    }

    @Override
    public void removeTrack(TrackLite track, UserLite user) throws Exception {
        if (!dataRepository.getUsersAndTracks().containsKey(user)) {
            throw new UserLiteNotFoundException();
        }
        if (!dataRepository.getUsersAndTracks().get(user).contains(track)) {
            throw new TrackLiteNotFoundException();
        }
        dataRepository.getUsersAndTracks().get(user).remove(track);
        this.notify(track, TrackLite.class, ModelUpdateTypes.DELETE_TRACK);

    }

    @Override
    public TrackLite getTrack(UUID trackId) throws TrackLiteNotFoundException {
        return dataRepository.getUsersAndTracks().values().stream()
                .toList()
                .stream()
                .flatMap(List::stream)
                .toList()
                .stream()
                .filter(trackLite -> trackLite.getId() == trackId)
                .findFirst()
                .orElseThrow(TrackLiteNotFoundException::new);
    }

    @Override
    public UserLite getOwner(UUID trackId) throws UserLiteNotFoundException, TrackLiteNotFoundException {

        TrackLite track = getTrack(trackId);

        return dataRepository.getUsersAndTracks().entrySet().stream()
                .filter(entry -> entry.getValue().contains(track))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(UserLiteNotFoundException::new);
    }

    @Override
    public void removeAllTracks(UserLite userLite) throws TrackLiteNotFoundException {
        if (!dataRepository.getUsersAndTracks().containsKey(userLite)) {
            throw new TrackLiteNotFoundException();
        }
        dataRepository.getUsersAndTracks().put(userLite, new ArrayList<>());
    }

    @Override
    public List<TrackLite> getAllTracks() {
        return dataRepository.getUsersAndTracks().values().stream()
                .toList()
                .stream()
                .flatMap(List::stream)
                .toList();
    }

    // Link between DownloadedTrack and UserLite(s)

    @Override
    public void addRequestedTrackForUser(UUID trackId, UserLite user) {
        if (!dataRepository.getRequestedTracksForUsers().containsKey(trackId)) {
            dataRepository.getRequestedTracksForUsers().put(trackId, new ArrayList<>());
        }
        dataRepository.getRequestedTracksForUsers().get(trackId).add(user);
    }

    @Override
    public void removeRequestedTrackForUser(UUID trackId, UserLite user) throws RequestedTrackNotFound{
        if (!dataRepository.getRequestedTracksForUsers().containsKey(trackId)) {
            throw new RequestedTrackNotFound();
        }
        dataRepository.getRequestedTracksForUsers().get(trackId).remove(user);

        // Remove trackId from map if no more users are waiting for it
        if (dataRepository.getRequestedTracksForUsers().get(trackId).isEmpty()) {
            dataRepository.getRequestedTracksForUsers().remove(trackId);
        }
    }

    @Override
    public List<UserLite> getUsersForRequestedTrack(UUID trackId) throws RequestedTrackNotFound {
        if (!dataRepository.getRequestedTracksForUsers().containsKey(trackId)) {
            throw new RequestedTrackNotFound();
        }
        return dataRepository.getRequestedTracksForUsers().get(trackId);
    }
}
