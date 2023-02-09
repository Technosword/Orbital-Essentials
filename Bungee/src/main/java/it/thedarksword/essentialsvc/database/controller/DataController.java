package it.thedarksword.essentialsvc.database.controller;

import it.thedarksword.essentialsvc.data.dao.DataDao;
import it.thedarksword.essentialsvc.data.domain.GenericData;
import it.thedarksword.essentialsvc.data.domain.HomeData;
import it.thedarksword.essentialsvc.data.domain.SpawnData;
import it.thedarksword.essentialsvc.data.domain.UserAccount;
import it.thedarksword.essentialsvc.database.Database;
import it.thedarksword.essentialsvc.database.DatabaseManager;
import it.thedarksword.essentialsvc.utils.ServerLocationObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class DataController {

    private final Database database;

    public DataController(Database database) {
        this.database = database;
    }

    public HashMap<UUID, UserAccount> loadUsers(DataDao<UUID, UserAccount> userDao) {
        HashMap<UUID, UserAccount> accounts = new HashMap<>();
        database.open();

        try {
            ResultSet resultSet = database.getStatement().executeQuery("SELECT * FROM " + DatabaseManager.PLAYERS_TABLE);
            while (resultSet.next()) {
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                String username = resultSet.getString("username");

                UserAccount account = new UserAccount(uuid, username);
                account.setDataDao(userDao);

                accounts.put(uuid, account);
            }
            resultSet.close();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }

        database.close();
        return accounts;
    }

    public HashMap<Integer, HomeData> loadHomes(DataDao<Integer, HomeData> homeDao) {
        ArrayList<HomeData> duplicates = new ArrayList<>();
        HashMap<Integer, HomeData> homes = new HashMap<>();
        database.open();

        try {
            ResultSet resultSet = database.getStatement().executeQuery("SELECT * FROM " + DatabaseManager.HOMES_TABLE);
            while (resultSet.next()) {
                int id = homes.size() + 1;
                String name = resultSet.getString("name");
                String server = resultSet.getString("server");
                UUID player = UUID.fromString(resultSet.getString("player_uuid"));
                String world = resultSet.getString("world");
                double x = resultSet.getDouble("x");
                double y = resultSet.getDouble("y");
                double z = resultSet.getDouble("z");
                float yaw = resultSet.getFloat("yaw");
                float pitch = resultSet.getFloat("pitch");

                HomeData homeData = new HomeData(id, name, server, player, new ServerLocationObject(server, world, x, y, z, yaw, pitch));
                homeData.setDataDao(homeDao);

                if (homes.values().stream().anyMatch(it -> it.getName().equals(name) && it.getPlayer().equals(player))) duplicates.add(homeData);
                else homes.put(id, homeData);
            }
            resultSet.close();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        database.close();

        duplicates.forEach(it -> {
            deleteData(it);
            homes.values().stream().filter(home -> home.getName().equals(it.getName()) && home.getPlayer().equals(it.getPlayer())).findFirst().ifPresent(this::saveData);
        });

        System.out.println("Loaded " + homes.size() + " homes and deleted " + duplicates.size() + " duplicates");
        return homes;
    }

    public HashMap<String, SpawnData> loadSpawns(DataDao<String, SpawnData> spawnDao) {
        HashMap<String, SpawnData> spawns = new HashMap<>();
        database.open();

        try {
            ResultSet resultSet = database.getStatement().executeQuery("SELECT * FROM " + DatabaseManager.SPAWN_TABLE);
            while (resultSet.next()) {
                String server = resultSet.getString("server");
                String world = resultSet.getString("world");
                double x = resultSet.getDouble("x");
                double y = resultSet.getDouble("y");
                double z = resultSet.getDouble("z");
                float yaw = resultSet.getFloat("yaw");
                float pitch = resultSet.getFloat("pitch");

                SpawnData spawnData = new SpawnData(server, new ServerLocationObject(server, world, x, y, z, yaw, pitch));
                spawnData.setDataDao(spawnDao);

                spawns.put(server, spawnData);
            }
            resultSet.close();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }

        database.close();
        return spawns;
    }

    public void saveData(GenericData data) {
        database.open();

        Statement statement = database.getStatement();
        try {
            ResultSet resultSet = statement.executeQuery(data.getDao().getSQLQuery(data));

            if (resultSet.next()) {
                if (data.getDao().getSQLUpdate(data) != null) statement.executeUpdate(data.getDao().getSQLUpdate(data));
            }
            else statement.execute(data.getDao().getSQLInsert(data));

            resultSet.close();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }

        database.close();
    }

    public void deleteData(GenericData data) {
        database.open();

        Statement statement = database.getStatement();
        try {
            statement.execute(data.getDao().getSQLDelete(data));
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }

        database.close();
    }

    public void saveAllUsers(Collection<UserAccount> values) {
        for (UserAccount account : values) {
            saveData(account);
        }
    }

    public void saveAllHomes(Collection<HomeData> values) {
        for (HomeData data : values) {
            saveData(data);
        }
    }

    public void saveAllSpawns(Collection<SpawnData> values) {
        for (SpawnData data : values) {
            saveData(data);
        }
    }

}