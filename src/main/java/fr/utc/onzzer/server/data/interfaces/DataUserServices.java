package fr.utc.onzzer.server.data.interfaces;

import java.util.UUID;
import fr.utc.onzzer.common.dataclass.UserLite;

public interface DataUserServices {
    UserLite getUser(UUID userId) throws Exception;

    void addUser(UserLite user) throws Exception;

    void deleteUser(UserLite user) throws Exception;

    void updateUser(UserLite user) throws Exception;
}
