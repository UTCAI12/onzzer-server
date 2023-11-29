package fr.utc.onzzer.server.data;

import fr.utc.onzzer.server.data.interfaces.DataTrackServices;
import fr.utc.onzzer.server.data.interfaces.DataUserServices;
import fr.utc.onzzer.server.data.interfaces.impl.DataTrackServicesImpl;
import fr.utc.onzzer.server.data.interfaces.impl.DataUserServicesImpl;

public class ServerController {
    private final DataRepository dataRepository = new DataRepository();
    private final DataUserServices dataUserServices = new DataUserServicesImpl(dataRepository);
    private final DataTrackServices dataTrackServices = new DataTrackServicesImpl(dataRepository);

    public DataUserServices getDataUserServices() {
        return dataUserServices;
    }

    public DataTrackServices getDataTrackServices() {
        return dataTrackServices;
    }
}
