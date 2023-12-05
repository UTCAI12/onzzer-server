package fr.utc.onzzer.server.data.interfaces;

import java.util.List;
import java.util.UUID;
import fr.utc.onzzer.common.dataclass.UserLite;
import fr.utc.onzzer.server.data.exceptions.UserLiteNotFoundException;
import fr.utc.onzzer.server.communication.ServerSocketManager;

public interface DataUserServices {
    UserLite getUser(UUID userId) throws UserLiteNotFoundException;

    void addUser(UserLite user, ServerSocketManager ssm);

    void deleteUser(UserLite user) throws UserLiteNotFoundException;

    void updateUser(UserLite user) throws UserLiteNotFoundException;

    ServerSocketManager getSocket(UserLite user) throws UserLiteNotFoundException;

    List<UserLite> getAllUsers();
}
