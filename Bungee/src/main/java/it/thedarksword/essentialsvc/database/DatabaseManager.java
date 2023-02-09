package it.thedarksword.essentialsvc.database;

import it.thedarksword.essentialsvc.config.DatabaseConfig;
import it.thedarksword.essentialsvc.database.controller.DataController;
import it.thedarksword.essentialsvc.database.impl.MySQLDatabaseImpl;
import it.thedarksword.essentialsvc.database.process.DataProcess;

import java.sql.SQLException;

public class DatabaseManager {

    public static String PLAYERS_TABLE = "essentialsvc_players";
    public static String HOMES_TABLE = "essentialsvc_homes";
    public static String SPAWN_TABLE = "essentialsvc_spawns";

    public Database database;
    public DataController controller;
    public DataProcess process;

    private final DatabaseConfig config;

    public DatabaseManager(DatabaseConfig config) {
        this.config = config;
    }

    public void init() {
        database = new MySQLDatabaseImpl(config);

        if (database.open()) {
            try {
                database.getStatement().execute(String.format("CREATE TABLE IF NOT EXISTS %s (uuid VARCHAR(36) NOT NULL UNIQUE, username VARCHAR(32) NOT NULL);", PLAYERS_TABLE));
                database.getStatement().execute(String.format("CREATE TABLE IF NOT EXISTS %s (id INT PRIMARY KEY AUTO_INCREMENT, player_uuid VARCHAR(36) NOT NULL, server VARCHAR(32) NOT NULL, name VARCHAR(32) NOT NULL, world VARCHAR(32) NOT NULL, x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT NOT NULL DEFAULT 0, pitch FLOAT NOT NULL DEFAULT 0, UNIQUE (player_uuid, name));", HOMES_TABLE));
                database.getStatement().execute(String.format("CREATE TABLE IF NOT EXISTS %s (id INT PRIMARY KEY AUTO_INCREMENT, server VARCHAR(32) NOT NULL UNIQUE, world VARCHAR(32) NOT NULL, x DOUBLE, y DOUBLE, z DOUBLE, yaw FLOAT NOT NULL DEFAULT 0, pitch FLOAT NOT NULL DEFAULT 0);", SPAWN_TABLE));
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
            database.close();
        }

        controller = new DataController(database);
        process = new DataProcess(controller);
    }

}
