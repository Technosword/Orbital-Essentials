package it.thedarksword.essentialsvc.config;

import it.thedarksword.essentialsvc.yaml.Configuration;

public class DatabaseConfig {

    private final Configuration config;

    public DatabaseConfig(Configuration config) {
        this.config = config;
    }

    public String getHostname() {
        return config.getString("mysql.host");
    }

    public Integer getPort() {
        return config.getInt("mysql.port");
    }

    public String getDatabase() {
        return config.getString("mysql.database");
    }

    public String getUsername() {
        return config.getString("mysql.username");
    }

    public String getPassword() {
        return config.getString("mysql.password");
    }

}