package ru.func.raidarea.database;

import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@AllArgsConstructor
public class MySQL extends DatabaseConnector {

    /* Класс хранящий инструменты для работы с MySql */
    private final String user;
    private final String password;
    private final String host;
    private final String database;
    private final int port;

    @Override
    public Connection openConnection() throws SQLException, ClassNotFoundException {
        if (checkConnection())
            return connection;
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?characterEncoding=UTF-8&autoReconnect=true", this.user, this.password);
        return connection;
    }
}
