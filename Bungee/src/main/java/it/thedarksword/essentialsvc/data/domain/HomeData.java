package it.thedarksword.essentialsvc.data.domain;

import it.thedarksword.essentialsvc.data.dao.DataDao;
import it.thedarksword.essentialsvc.utils.ServerLocationObject;
import lombok.Data;

import java.util.UUID;

@Data
public class HomeData implements GenericData<Integer, HomeData> {

    private DataDao<Integer, HomeData> dataDao;

    Integer id;
    String name;
    String server;
    UUID player;
    ServerLocationObject locationObject;

    public HomeData(Integer id, String name, String server, UUID player, ServerLocationObject locationObject) {
        this.id = id;
        this.name = name;
        this.server = server;
        this.player = player;
        this.locationObject = locationObject;
    }

    @Override
    public DataDao<Integer, HomeData> getDao() {
        return dataDao;
    }

    @Override
    public HomeData getData() {
        return this;
    }

    public String getName() {
        return name;
    }

    public String getServer() {
        return server;
    }

    public UUID getPlayer() {
        return player;
    }

    public ServerLocationObject getLocationObject() {
        return locationObject;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setLocationObject(ServerLocationObject locationObject) {
        this.locationObject = locationObject;
    }

    public void setDataDao(DataDao<Integer, HomeData> dataDao) {
        this.dataDao = dataDao;
    }

}
