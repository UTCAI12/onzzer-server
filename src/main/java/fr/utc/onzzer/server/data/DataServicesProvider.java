package fr.utc.onzzer.server.data;

import fr.utc.onzzer.server.data.impl.DataTrackServicesImpl;
import fr.utc.onzzer.server.data.impl.DataUserServicesImpl;

public class DataServicesProvider {
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
