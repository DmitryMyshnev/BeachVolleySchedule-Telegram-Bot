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
    private final SQLiteConfig config;
    private final String pathDbConnect;
    private static Connection connect;

    public ConnectionDb() {
        pathDbConnect = getPassFromFileConfig();
        config = new SQLiteConfig();
        config.setSharedCache(true);
        connect =  getConnection(config,pathDbConnect);
    }

    public static void executeUpdate(String query) throws SQLException {
            Statement statement = connect.createStatement();
            statement.executeUpdate(query);
            statement.close();
            //connect.close();
    }

    public static ResultSet executeQuery(String query) {
        try {
            ResultSet res = connect.createStatement().executeQuery(query);
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void execute(String query) throws SQLException {
        Statement statement = connect.createStatement();
        statement.execute(query);
        statement.close();
       // connect.close();
    }
    private  Connection getConnection(SQLiteConfig config,String path) {
        try {
            Class.forName("org.sqlite.JDBC");
            return config.createConnection(path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private  String getPassFromFileConfig() {
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
