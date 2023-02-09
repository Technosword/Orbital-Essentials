package it.thedarksword.essentialsvc.data.domain;

import it.thedarksword.essentialsvc.data.dao.DataDao;
import it.thedarksword.essentialsvc.utils.ServerLocationObject;
import lombok.Data;

@Data
public class SpawnData implements GenericData<String, SpawnData> {

    DataDao<String, SpawnData> dataDao;

    String server;
    ServerLocationObject locationObject;

    public SpawnData(String server, ServerLocationObject locationObject) {
        this.server = server;
        this.locationObject = locationObject;
    }

    @Override
    public DataDao<String, SpawnData> getDao() {
        return dataDao;
    }

    @Override
    public SpawnData getData() {
        return this;
    }

    public String getServer() {
        return server;
    }

    public ServerLocationObject getLocationObject() {
        return locationObject;
    }

    public void setDataDao(DataDao<String, SpawnData> dataDao) {
        this.dataDao = dataDao;
    }

    public void setLocationObject(ServerLocationObject locationObject) {
        this.locationObject = locationObject;
    }

}
