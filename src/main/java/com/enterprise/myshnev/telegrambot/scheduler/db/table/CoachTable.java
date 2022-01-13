package com.enterprise.myshnev.telegrambot.scheduler.db.table;

import com.enterprise.myshnev.telegrambot.scheduler.db.ConnectionDb;
import com.enterprise.myshnev.telegrambot.scheduler.db.CrudDb;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.Coach;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.TelegramUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.units.qual.C;
import org.sqlite.SQLiteConfig;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.enterprise.myshnev.telegrambot.scheduler.db.CommandQuery.*;
import static com.enterprise.myshnev.telegrambot.scheduler.db.DbStatusResponse.OK;

public class CoachTable implements CrudDb<Coach> {
    private static final String CHAT_ID = "chat_id";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    public static Logger LOGGER = LogManager.getLogger(CoachTable.class);

    @Override
    public String addTable(String name) {
        return null;
    }

    @Override
    public String insertIntoTable(String tableName, Coach coach) {
        String query = String.format(INSERT_INTO.getQuery(), tableName, CHAT_ID + "," + FIRST_NAME + "," + LAST_NAME, coach);
        try {
            ConnectionDb.executeUpdate(query);
        } catch (SQLException e) {
            LOGGER.info(e.getMessage());
            LOGGER.info(e.getSQLState());
        }
        return OK.getStatus();
    }

    @Override
    public List<Coach> findAll(String tableName) {
        String query = String.format(SELECT_ALL.getQuery(), tableName);
        try ( ResultSet res = ConnectionDb.executeQuery(query)){
            List<Coach> list = new ArrayList<>();
            while (res.next()) {
                Coach coach = new Coach();
                coach.setChatId(res.getString(CHAT_ID));
                coach.setFirstName(res.getString(FIRST_NAME));
                String lastName = res.getString(LAST_NAME);
                coach.setLastName(lastName.equals("null") ?"":lastName);
                list.add(coach);
            }
            res.getStatement().close();
            res.close();
            return list;
        } catch (SQLException e) {
            LOGGER.info(e.getMessage());
            LOGGER.info(e.getSQLState());
            return null;
        }
    }

    @Override
    public Optional<Coach> findById(String tableName, String id) {
        String query = String.format(SELECT_WHERE.getQuery(), tableName,CHAT_ID, id);
        try (ResultSet res = ConnectionDb.executeQuery(query)){
            if (res.next()) {
                Coach coach = new Coach();
                coach.setChatId(res.getString(CHAT_ID));
                coach.setFirstName(res.getString(FIRST_NAME));
                String lastName = res.getString(LAST_NAME);
                coach.setLastName(lastName == null ?"":lastName);
                res.getStatement().close();
                res.close();
                return Optional.of(coach);
            }
            return Optional.empty();
        } catch (SQLException e) {
            LOGGER.info(e.getMessage());
            LOGGER.info(e.getSQLState());
            return Optional.empty();
        }
    }

    @Override
    public List<Coach> findBy(String tableName, String column, Object arg) {
        return null;
    }

    @Override
    public String update(String tableName,String chatId, String arg, String value) {
        return null;
    }

    @Override
    public String delete(String tableName,String id) {
        return null;
    }

    @Override
    public Integer count(String tableName) {
        return null;
    }

    @Override
    public void dropTable(String tableName) {

    }
}
