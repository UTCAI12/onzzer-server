package fr.utc.onzzer.server.data;

import fr.utc.onzzer.common.dataclass.TrackLite;
import fr.utc.onzzer.common.dataclass.UserLite;
import fr.utc.onzzer.server.data.exceptions.RequestedTrackNotFound;
import fr.utc.onzzer.server.data.exceptions.TrackLiteNotFoundException;
import fr.utc.onzzer.server.data.impl.DataTrackServicesImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.UUID;

public class DataTrackServicesTest {

    private DataTrackServices dataTrackServicesUnderTest;

    @BeforeEach
    public void setUp() {
        DataRepository dataRepository = new DataRepository();
        dataTrackServicesUnderTest = new DataTrackServicesImpl(dataRepository);
    }

    @Test
    void testAddAndGetAllDataTrackServices() {
        // Setup
        final TrackLite track = new TrackLite(UUID.randomUUID(), UUID.randomUUID(), "title", "artist", "Album");
        final TrackLite track2 = new TrackLite(UUID.randomUUID(), UUID.randomUUID(), "title2", "artist2", "Album");
        final UserLite user = new UserLite(UUID.randomUUID(), "User1");
        final UserLite user2 = new UserLite(UUID.randomUUID(), "User2");

        // Run the test
        dataTrackServicesUnderTest.addTrack(track, user);
        dataTrackServicesUnderTest.addTrack(track2, user);
        dataTrackServicesUnderTest.addTrack(track2, user2);

        // test to add a track with the same id and user ( should not be added )
        dataTrackServicesUnderTest.addTrack(track, user);

        // verify if track added twice
        Assertions.assertEquals(dataTrackServicesUnderTest.getAllTracks().size(), 3);

    }

    @Test
    void testUpdateTrack() throws TrackLiteNotFoundException {
        // Setup
        final UUID trackUUID = UUID.randomUUID();
        final TrackLite track = new TrackLite(trackUUID, UUID.randomUUID(), "title", "artist", "Album");
        final TrackLite track2 = new TrackLite(trackUUID, UUID.randomUUID(), "title2", "artist2", "Album");
        final UserLite user = new UserLite(UUID.randomUUID(), "User1");

        // Run the test
        dataTrackServicesUnderTest.addTrack(track, user);

        try {
            dataTrackServicesUnderTest.updateTrack(track2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Verify the results
        Assertions.assertEquals( dataTrackServicesUnderTest.getTrack(trackUUID).getTitle(), "title2");
    }

    @Test
    void testDeleteTrack() throws Exception {
        // Setup
        final UUID trackUUID = UUID.randomUUID();
        final TrackLite track = new TrackLite(trackUUID, UUID.randomUUID(), "title", "artist", "Album");
        final UserLite user = new UserLite(UUID.randomUUID(), "User1");

        // Run the test
        dataTrackServicesUnderTest.addTrack(track, user);
        dataTrackServicesUnderTest.removeTrack(track, user);

        // Verify the results
        Assertions.assertEquals(dataTrackServicesUnderTest.getAllTracks().size(), 0);
    }

    @Test
    void testGetTrack() throws TrackLiteNotFoundException {
        // Setup
        final UUID trackUUID = UUID.randomUUID();
        final TrackLite track = new TrackLite(trackUUID, UUID.randomUUID(), "title", "artist", "album");
        final UserLite user = new UserLite(UUID.randomUUID(), "User1");

        // Run the test
        dataTrackServicesUnderTest.addTrack(track, user);

        // Verify the results
        Assertions.assertEquals(dataTrackServicesUnderTest.getTrack(trackUUID).getId(), track.getId());
    }

    @Test
    void testGetOwner() throws Exception {
        // Setup
        final UUID trackUUID = UUID.randomUUID();
        final TrackLite track = new TrackLite(trackUUID, UUID.randomUUID(), "title", "artist", "Album");
        final UserLite user = new UserLite(UUID.randomUUID(), "User1");

        // Run the test
        dataTrackServicesUnderTest.addTrack(track, user);

        // Verify the results
        Assertions.assertEquals(dataTrackServicesUnderTest.getOwner(trackUUID).getId(), user.getId());
    }

    @Test
    void testRemoveAllTracksByUser() throws Exception {
        // Setup
        final UserLite user = new UserLite(UUID.randomUUID(), "User1");

        //for loop to add 10 tracks and users
        for (int i = 0; i < 10; i++) {
            final TrackLite track = new TrackLite(UUID.randomUUID(), UUID.randomUUID(), "title", "artist", "Album");
            dataTrackServicesUnderTest.addTrack(track, user);
        }


        dataTrackServicesUnderTest.removeAllTracks(user);

        // Verify the results
        Assertions.assertEquals(dataTrackServicesUnderTest.getAllTracks().size(), 0);
    }


    // TEST MAP THAT LINKS REQUESTED TRACKS TO USERS
    @Test
    void testAddAndGetRequestedTrack() throws RequestedTrackNotFound {
        // Setup
        final UUID trackUUID = UUID.randomUUID();
        final UserLite user = new UserLite(UUID.randomUUID(), "User1");
        final UserLite user2 = new UserLite(UUID.randomUUID(), "User2");

        // Run the test
        dataTrackServicesUnderTest.addRequestedTrackForUser(trackUUID, user);
        dataTrackServicesUnderTest.addRequestedTrackForUser(trackUUID, user2);

        // Verify the results
        Assertions.assertEquals(dataTrackServicesUnderTest.getUsersForRequestedTrack(trackUUID).size(), 2);
    }

    @Test
    void testRemoveRequestedTrackForUser() throws RequestedTrackNotFound {
        // Setup
        final UUID trackUUID = UUID.randomUUID();
        final UserLite user = new UserLite(UUID.randomUUID(), "User1");
        final UserLite user2 = new UserLite(UUID.randomUUID(), "User2");

        // Run the test
        dataTrackServicesUnderTest.addRequestedTrackForUser(trackUUID, user);
        dataTrackServicesUnderTest.addRequestedTrackForUser(trackUUID, user2);

        dataTrackServicesUnderTest.removeRequestedTrackForUser(trackUUID, user);

        // Verify the results
        Assertions.assertEquals(dataTrackServicesUnderTest.getUsersForRequestedTrack(trackUUID).size(), 1);
    }
}
