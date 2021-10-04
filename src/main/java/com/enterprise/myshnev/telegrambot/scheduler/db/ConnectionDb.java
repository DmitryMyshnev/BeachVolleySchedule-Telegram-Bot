package com.enterprise.myshnev.telegrambot.scheduler.db;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ResourceUtils;
import org.sqlite.SQLiteConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

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
            return config.createConnection(getPassFromFileConfig() );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private static String getPassFromFileConfig() {
        Properties properties = new Properties();
        try {
            File file = ResourceUtils.getFile("classpath:application.properties");
            InputStream in = new FileInputStream(file);
            properties.load(in);
        } catch (IOException e) {
            e.getMessage();
        }
        return properties.getProperty("connectionDbUrl");
    }
}
