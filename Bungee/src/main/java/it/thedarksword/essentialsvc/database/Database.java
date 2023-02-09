package it.thedarksword.essentialsvc.database;

import java.sql.Connection;
import java.sql.Statement;

public interface Database {

    public abstract Connection getConnection();
    public abstract Statement getStatement();

    public abstract String getType();

    public abstract boolean open();
    public abstract boolean close();
    public abstract boolean isConnected();

}