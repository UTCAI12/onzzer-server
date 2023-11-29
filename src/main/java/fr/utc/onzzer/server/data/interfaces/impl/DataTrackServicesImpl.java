package fr.utc.onzzer.server.data.interfaces.impl;

import fr.utc.onzzer.common.dataclass.TrackLite;
import fr.utc.onzzer.common.dataclass.UserLite;
import fr.utc.onzzer.server.data.DataRepository;
import fr.utc.onzzer.server.data.exceptions.TrackLiteNotFoundException;
import fr.utc.onzzer.server.data.interfaces.DataTrackServices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class DataTrackServicesImpl implements DataTrackServices {
    private final DataRepository dataRepository;

    public DataTrackServicesImpl(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @Override
    public void updateTrack(TrackLite newTrack) throws TrackLiteNotFoundException {
        Collection<List<TrackLite>> values = dataRepository.getUsersAndTracks().values();
        for (List<TrackLite> userTrackLites : values) {
            for (TrackLite trackLite: userTrackLites) {
                if (trackLite.getId() == newTrack.getId()) {
                    userTrackLites.add(newTrack);
                    userTrackLites.remove(trackLite);
                    return;
                }
            }
        }
        throw new TrackLiteNotFoundException();
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
    public void removeAllTracks(UserLite userLite) throws TrackLiteNotFoundException {
        if (!dataRepository.getUsersAndTracks().containsKey(userLite)) {
            throw new TrackLiteNotFoundException();
        }
        dataRepository.getUsersAndTracks().put(userLite, new ArrayList<>());
    }
}
