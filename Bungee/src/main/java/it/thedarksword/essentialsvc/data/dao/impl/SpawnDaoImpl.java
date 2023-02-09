package it.thedarksword.essentialsvc.data.dao.impl;

import it.thedarksword.essentialsvc.data.dao.DataDao;
import it.thedarksword.essentialsvc.data.domain.SpawnData;
import it.thedarksword.essentialsvc.database.DatabaseManager;
import it.thedarksword.essentialsvc.database.process.task.DataProcessTask;
import lombok.Data;

import java.util.AbstractMap;
import java.util.HashMap;

public class SpawnDaoImpl implements DataDao<String, SpawnData> {

    public HashMap<String, SpawnData> spawns = new HashMap<>();

    private final DatabaseManager databaseManager;

    public SpawnDaoImpl(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public HashMap<String, SpawnData> load() {
        spawns = new HashMap<>(databaseManager.controller.loadSpawns(this));
        return spawns;
    }

    @Override
    public HashMap<String, SpawnData> getData() {
        return spawns;
    }

    @Override
    public void create(SpawnData data) {
        data.setDataDao(this);

        spawns.put(data.getServer(), data);
        save(data);
    }

    @Override
    public void delete(SpawnData data) {
        spawns.remove(data.getServer());
        databaseManager.process.task.queue.add(new AbstractMap.SimpleEntry<>(data, false));
        DataProcessTask.startTask(databaseManager.process.task);
    }

    @Override
    public void save(SpawnData data) {
        data.setDataDao(this);
        spawns.put(data.getServer(), data);

        databaseManager.process.task.queue.add(new AbstractMap.SimpleEntry<>(data, true));
        DataProcessTask.startTask(databaseManager.process.task);
    }

    @Override
    public void saveAll() {
        DataProcessTask.stopTask(databaseManager.process.task);
        databaseManager.controller.saveAllSpawns(spawns.values());
    }

    @Override
    public String getSQLQuery(SpawnData data) {
        return String.format("SELECT * FROM %s WHERE server='%s'", DatabaseManager.SPAWN_TABLE, data.getServer());
    }

    @Override
    public String getSQLInsert(SpawnData data) {
        return String.format("INSERT INTO %s (server, world, x, y, z, yaw, pitch) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s')", DatabaseManager.SPAWN_TABLE, data.getServer(), data.getLocationObject().getWorld(), data.getLocationObject().getX(), data.getLocationObject().getY(), data.getLocationObject().getZ(), data.getLocationObject().getYaw(), data.getLocationObject().getPitch());
    }

    @Override
    public String getSQLUpdate(SpawnData data) {
        return String.format("UPDATE %s SET world='%s', x='%s', y='%s', z='%s', yaw='%s', pitch='%s' WHERE server='%s'", DatabaseManager.SPAWN_TABLE, data.getLocationObject().getWorld(), data.getLocationObject().getX(), data.getLocationObject().getY(), data.getLocationObject().getZ(), data.getLocationObject().getYaw(), data.getLocationObject().getPitch(), data.getServer());
    }

    @Override
    public String getSQLDelete(SpawnData data) {
        return String.format("DELETE FROM %s WHERE server='%s'", DatabaseManager.SPAWN_TABLE, data.getServer());
    }

}
