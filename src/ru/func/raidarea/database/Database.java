package ru.func.raidarea.database;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class Database {

    Connection connection;

    Database() {
        this.connection = null;
    }

    public abstract Connection openConnection() throws SQLException, ClassNotFoundException;

    boolean checkConnection() throws SQLException {
        return connection != null && !connection.isClosed();
    }
}
