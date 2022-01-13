package com.enterprise.myshnev.telegrambot.scheduler.db;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sqlite.SQLiteConfig;
import org.hibernate.dialect.Dialect;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionDb{
    private static Logger LOGGER = LogManager.getLogger(ConnectionDb.class);
    private static Connection connect;
   // private static final Lock lock = new ReentrantLock();


    public ConnectionDb() {
        String DbPath = "jdbc:sqlite:" + System.getProperty("user.dir") + File.separator + "user.db";
        SQLiteConfig config = new SQLiteConfig();
        config.setSharedCache(true);
        connect =  getConnection(config,DbPath);
    }

    public static void executeUpdate(String query) throws SQLException {

            Statement statement = connect.createStatement();
            statement.executeUpdate(query);
            statement.close();

    }

    public  static ResultSet executeQuery(String query) {
        try {
            return connect.createStatement().executeQuery(query);
        } catch (SQLException e) {
           LOGGER.info(e.getMessage());
           LOGGER.info(e.getSQLState());
            return null;
        }
    }
    public static void execute(String query) throws SQLException {
        Statement statement = connect.createStatement();
        statement.execute(query);
        statement.close();
    }
    private  Connection getConnection(SQLiteConfig config,String path) {
        try {
            Class.forName("org.sqlite.JDBC");
            return config.createConnection(path);
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
            return null;
        }
    }
}
