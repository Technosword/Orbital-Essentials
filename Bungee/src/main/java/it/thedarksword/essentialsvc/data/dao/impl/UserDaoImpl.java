package it.thedarksword.essentialsvc.data.dao.impl;

import it.thedarksword.essentialsvc.data.dao.DataDao;
import it.thedarksword.essentialsvc.data.domain.UserAccount;
import it.thedarksword.essentialsvc.database.DatabaseManager;
import it.thedarksword.essentialsvc.database.process.task.DataProcessTask;
import lombok.Data;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.UUID;

public class UserDaoImpl implements DataDao<UUID, UserAccount> {

    public HashMap<UUID, UserAccount> accounts = new HashMap<>();

    private final DatabaseManager databaseManager;

    public UserDaoImpl(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public HashMap<UUID, UserAccount> load() {
        accounts = new HashMap<>(databaseManager.controller.loadUsers(this));
        return accounts;
    }

    @Override
    public HashMap<UUID, UserAccount> getData() {
        return accounts;
    }

    @Override
    public void create(UserAccount data) {
        accounts.put(data.getUuid(), data);
        save(data);
    }

    @Override
    public void delete(UserAccount data) {
        accounts.remove(data.getUuid());
        databaseManager.process.task.queue.add(new AbstractMap.SimpleEntry<>(data, false));
        DataProcessTask.startTask(databaseManager.process.task);
    }

    @Override
    public void save(UserAccount data) {
        data.setDataDao(this);
        accounts.put(data.getUuid(), data);

        databaseManager.process.task.queue.add(new AbstractMap.SimpleEntry<>(data, true));
        DataProcessTask.startTask(databaseManager.process.task);
    }

    @Override
    public void saveAll() {
        DataProcessTask.stopTask(databaseManager.process.task);
        databaseManager.controller.saveAllUsers(accounts.values());
    }

    @Override
    public String getSQLQuery(UserAccount account) {
        return String.format("SELECT * FROM %s WHERE uuid='%s'", DatabaseManager.PLAYERS_TABLE, account.getUuid());
    }

    @Override
    public String getSQLInsert(UserAccount account) {
        return String.format("INSERT INTO %s (uuid, username) VALUES ('%s', '%s')", DatabaseManager.PLAYERS_TABLE, account.getUuid(), account.getName());
    }

    @Override
    public String getSQLUpdate(UserAccount account) {
        return String.format("UPDATE %s SET username='%s' WHERE uuid='%s'", DatabaseManager.PLAYERS_TABLE, account.getName(), account.getUuid());
    }

    @Override
    public String getSQLDelete(UserAccount account) {
        return String.format("DELETE FROM %s WHERE uuid='%s'", DatabaseManager.PLAYERS_TABLE, account.getUuid());
    }

}
