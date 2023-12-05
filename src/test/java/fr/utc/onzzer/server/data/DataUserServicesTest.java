package fr.utc.onzzer.server.data;

import fr.utc.onzzer.common.dataclass.TrackLite;
import fr.utc.onzzer.common.dataclass.UserLite;
import fr.utc.onzzer.server.communication.ServerSocketManager;
import fr.utc.onzzer.server.data.exceptions.RequestedTrackNotFound;
import fr.utc.onzzer.server.data.exceptions.TrackLiteNotFoundException;
import fr.utc.onzzer.server.data.interfaces.DataTrackServices;
import fr.utc.onzzer.server.data.interfaces.DataUserServices;
import fr.utc.onzzer.server.data.interfaces.impl.DataTrackServicesImpl;
import fr.utc.onzzer.server.data.interfaces.impl.DataUserServicesImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.Socket;
import java.util.UUID;

public class DataUserServicesTest {

    private DataUserServices dataUserServicesUnderTest;

    @BeforeEach
    public void setUp() {
        DataRepository dataRepository = new DataRepository();
        dataUserServicesUnderTest = new DataUserServicesImpl(dataRepository);
    }

    @Test
    void testAddAndGetAllDataUserServices() {
        // Setup
        final UserLite user = new UserLite(UUID.randomUUID(), "User1");
        final UserLite user2 = new UserLite(UUID.randomUUID(), "User2");

        // Run the test
        dataUserServicesUnderTest.addUser(user, null);
        dataUserServicesUnderTest.addUser(user2, null);

        // verify if track added twice
        Assertions.assertEquals(dataUserServicesUnderTest.getAllUsers().size(), 2);

    }

    @Test
    void testUpdateUser() throws Exception {
        // Setup
        final UUID userUUID = UUID.randomUUID();
        final UserLite user = new UserLite(userUUID, "User1");
        final UserLite user2 = new UserLite(userUUID, "User2");

        // Run the test
        dataUserServicesUnderTest.addUser(user, null);

        dataUserServicesUnderTest.updateUser(user2);

        Assertions.assertEquals(dataUserServicesUnderTest.getUser(userUUID).getUsername(), "User2");

    }

    @Test
    void testDeleteUser() throws Exception {
        // Setup
        final UUID userUUID = UUID.randomUUID();
        final UserLite user = new UserLite(userUUID, "User1");

        // Run the test
        dataUserServicesUnderTest.addUser(user, null);

        dataUserServicesUnderTest.deleteUser(user);

        Assertions.assertEquals(dataUserServicesUnderTest.getAllUsers().size(), 0);

    }

    @Test
    void testGetAllUsers() {
        // Setup
        final UserLite user = new UserLite(UUID.randomUUID(), "User1");
        final UserLite user2 = new UserLite(UUID.randomUUID(), "User2");

        // Run the test
        dataUserServicesUnderTest.addUser(user, null);
        dataUserServicesUnderTest.addUser(user2, null);

        // Verify the results
        Assertions.assertEquals(dataUserServicesUnderTest.getAllUsers().size(), 2);
    }


}
