package it.thedarksword.essentialsvc.data.dao.impl;

import it.thedarksword.essentialsvc.data.dao.DataDao;
import it.thedarksword.essentialsvc.data.domain.HomeData;
import it.thedarksword.essentialsvc.database.DatabaseManager;
import it.thedarksword.essentialsvc.database.process.task.DataProcessTask;

import java.util.*;

public class HomeDaoImpl implements DataDao<Integer, HomeData> {

    private HashMap<Integer, HomeData> homes = new HashMap<>();

    private final DatabaseManager databaseManager;

    public HomeDaoImpl(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public HashMap<Integer, HomeData> load() {
        homes = new HashMap<>(databaseManager.controller.loadHomes(this));
        return homes;
    }

    public Optional<HomeData> get(String name, UUID player) {
        return homes.values().stream().filter(it -> it.getPlayer().equals(player) && it.getName().equalsIgnoreCase(name)).findFirst();
    }

    @Override
    public HashMap<Integer, HomeData> getData() {
        return homes;
    }

    @Override
    public void create(HomeData data) {
        data.setDataDao(this);

        homes.put(data.getId(), data);
        save(data);
    }

    @Override
    public void delete(HomeData data) {
        homes.remove(data.getId());

        databaseManager.process.task.queue.add(new AbstractMap.SimpleEntry<>(data, false));
        DataProcessTask.startTask(databaseManager.process.task);
    }

    @Override
    public void save(HomeData data) {
        data.setDataDao(this);
        homes.put(data.getId(), data);

        databaseManager.process.task.queue.add(new AbstractMap.SimpleEntry<>(data, true));
        DataProcessTask.startTask(databaseManager.process.task);
    }

    @Override
    public void saveAll() {
        DataProcessTask.stopTask(databaseManager.process.task);
        databaseManager.controller.saveAllHomes(homes.values());
    }

    @Override
    public String getSQLQuery(HomeData data) {
        return String.format("SELECT * FROM %s WHERE name='%s' AND player_uuid='%s'", DatabaseManager.HOMES_TABLE, data.getName(), data.getPlayer());
    }

    @Override
    public String getSQLInsert(HomeData data) {
        return String.format("INSERT INTO %s (name, server, player_uuid, world, x, y, z, yaw, pitch) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')", DatabaseManager.HOMES_TABLE, data.getName(), data.getServer(), data.getPlayer(), data.getLocationObject().getWorld(), data.getLocationObject().getX(), data.getLocationObject().getY(), data.getLocationObject().getZ(), data.getLocationObject().getYaw(), data.getLocationObject().getPitch());
    }

    @Override
    public String getSQLUpdate(HomeData data) {
        return String.format("UPDATE %s SET server='%s', world='%s', x='%s', y='%s', z='%s', yaw='%s', pitch='%s' WHERE player_uuid='%s' AND name='%s'", DatabaseManager.HOMES_TABLE, data.getServer(), data.getLocationObject().getWorld(), data.getLocationObject().getX(), data.getLocationObject().getY(), data.getLocationObject().getZ(), data.getLocationObject().getYaw(), data.getLocationObject().getPitch(), data.getPlayer(), data.getName());
    }

    @Override
    public String getSQLDelete(HomeData data) {
        return String.format("DELETE FROM %s WHERE name='%s' AND player_uuid='%s'", DatabaseManager.HOMES_TABLE, data.getName(), data.getPlayer());
    }

}