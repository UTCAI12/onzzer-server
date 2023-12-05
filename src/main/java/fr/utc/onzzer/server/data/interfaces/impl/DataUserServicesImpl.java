package fr.utc.onzzer.server.data.interfaces.impl;

import fr.utc.onzzer.common.dataclass.TrackLite;
import fr.utc.onzzer.common.dataclass.User;
import fr.utc.onzzer.common.dataclass.UserLite;
import fr.utc.onzzer.server.data.DataRepository;
import fr.utc.onzzer.server.data.exceptions.UserLiteNotFoundException;
import fr.utc.onzzer.server.data.interfaces.DataUserServices;
import fr.utc.onzzer.server.communication.ServerSocketManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DataUserServicesImpl implements DataUserServices {
    private final DataRepository dataRepository;

    public DataUserServicesImpl(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @Override
    public UserLite getUser(UUID userId) throws UserLiteNotFoundException {
        return dataRepository.getUsersAndTracks().keySet().stream()
                .filter((userLite -> userLite.getId() == userId))
                .findFirst()
                .orElseThrow(UserLiteNotFoundException::new);
    }

    @Override
    public void addUser(UserLite user, ServerSocketManager ssm) {
        dataRepository.getUsersAndTracks().put(user, new ArrayList<>());
        dataRepository.getUsersAndSockets().put(user, ssm);
    }

    @Override
    public void deleteUser(UserLite user) throws UserLiteNotFoundException {
        if (!dataRepository.getUsersAndTracks().containsKey(user)) {
            throw new UserLiteNotFoundException();
        }
        dataRepository.getUsersAndTracks().remove(user);
        dataRepository.getUsersAndSockets().remove(user);
    }

    @Override
    public void updateUser(UserLite newUser) throws UserLiteNotFoundException {
        UserLite previousUser = dataRepository.getUsersAndTracks().keySet().stream()
                .filter(userLite -> userLite.getId() == newUser.getId())
                .findFirst()
                .orElseThrow(UserLiteNotFoundException::new);

        List<TrackLite> trackLites = dataRepository.getUsersAndTracks().get(previousUser);
        dataRepository.getUsersAndTracks().put(newUser, trackLites);
        dataRepository.getUsersAndTracks().remove(previousUser);

        ServerSocketManager socket = dataRepository.getUsersAndSockets().get(previousUser);
        dataRepository.getUsersAndSockets().put(newUser, socket);
        dataRepository.getUsersAndSockets().remove(previousUser);
    }

    @Override
    public List<UserLite> getAllUsers() {
        return dataRepository.getUsersAndTracks().keySet().stream().toList();
    }

    public ServerSocketManager getSocket(UserLite user) {
        return dataRepository.getUsersAndSockets().get(user);
    }
}

