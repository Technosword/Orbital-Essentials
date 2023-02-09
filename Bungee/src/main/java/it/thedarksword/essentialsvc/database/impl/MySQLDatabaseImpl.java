package it.thedarksword.essentialsvc.database.impl;

import it.thedarksword.essentialsvc.config.DatabaseConfig;
import it.thedarksword.essentialsvc.database.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLDatabaseImpl implements Database {

    private String url, user, password;

    public MySQLDatabaseImpl(DatabaseConfig config) {
        this(config.getHostname(), config.getPort(),
                config.getDatabase(),
                config.getUsername(), config.getPassword());
    }

    public MySQLDatabaseImpl(String host, int port, String database, String user, String password) {
        this.url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false";
        this.user = user;
        this.password = password;
    }

    private Connection connection;
    private Statement statement;

    public Connection getConnection() {
        return connection;
    }

    @Override
    public Statement getStatement() {
        return statement;
    }

    @Override
    public String getType() {
        return "MySQL";
    }

    @Override
    public boolean open() {
        if (isConnected()) return true;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            if (this.connection == null) {
                this.connection = DriverManager.getConnection(url, user, password);
            }
            if (this.statement == null && this.connection != null) {
                this.statement = this.connection.createStatement();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return isConnected();
    }

    @Override
    public boolean close() {
        if (isConnected()) {
            try {
                if (this.statement != null)
                    this.statement.close();

                if (this.connection != null)
                    this.connection.close();

                this.statement = null;
                this.connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return isConnected();
    }

    @Override
    public boolean isConnected() {
        return this.connection != null;
    }

}