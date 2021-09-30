package com.enterprise.myshnev.telegrambot.scheduler.db;


import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionDb {

    public static void executeUpdate(String query, SQLiteConfig config) throws SQLException {
            Connection connect = getConnection(config);
            Statement statement = connect.createStatement();
            statement.executeUpdate(query);
            statement.close();
            connect.close();
    }

    public static ResultSet executeQuery(String query, SQLiteConfig config) {
        try {
            ResultSet res = getConnection(config).createStatement().executeQuery(query);
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Connection getConnection(SQLiteConfig config) {
        try {
            Class.forName("org.sqlite.JDBC");
            return config.createConnection("jdbc:sqlite:src/main/resources/user.db");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
