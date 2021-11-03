package com.enterprise.myshnev.telegrambot.scheduler.db.table;

import com.enterprise.myshnev.telegrambot.scheduler.db.ConnectionDb;
import com.enterprise.myshnev.telegrambot.scheduler.db.CrudDb;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.TelegramUser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.enterprise.myshnev.telegrambot.scheduler.db.CommandQuery.*;
import static com.enterprise.myshnev.telegrambot.scheduler.db.DbStatusResponse.*;

public class AdminTable implements CrudDb<TelegramUser> {
    private static final String TABLE = "Admin";
    private static final String CHAT_ID = "chat_id";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";

    public AdminTable() {

    }

    @Override
    public String insertIntoTable(String tableName,TelegramUser telegramUser) {
        if (findById(tableName,telegramUser.getChatId()).isEmpty()) {
            String query = String.format(INSERT_INTO.getQuery(), tableName, CHAT_ID + "," + FIRST_NAME + "," + LAST_NAME, telegramUser.fullName());
            try {
                ConnectionDb.executeUpdate(query);
                return OK.getStatus();
            } catch (SQLException e) {
               return FAIL.getStatus();
            }
        }
        return EXIST.getStatus();
    }

    @Override
    public List<TelegramUser> findAll(String tableName) {
        try {
            String query = String.format(SELECT_ALL.getQuery(), TABLE);
            ResultSet res = ConnectionDb.executeQuery(query);
            List<TelegramUser> list = new ArrayList<>();
            while (res.next()) {
                TelegramUser user = new TelegramUser();
                user.setChatId(res.getString(CHAT_ID));
                user.setFirstName(res.getString(FIRST_NAME));
                user.setLastName(res.getString(LAST_NAME));
                list.add(user);
            }
            res.getStatement().close();
            res.close();
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Optional<TelegramUser> findById(String tableName,String id) {
        try {
            String query = String.format(SELECT_WHERE.getQuery(), tableName,CHAT_ID, id);
            ResultSet res = ConnectionDb.executeQuery(query);
            if (res.next()) {
                TelegramUser user = new TelegramUser();
                user.setChatId(res.getString(CHAT_ID));
                user.setFirstName(res.getString(FIRST_NAME));
                user.setLastName(res.getString(LAST_NAME));
                res.getStatement().close();
                res.close();
                return Optional.of(user);
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<TelegramUser> findBy(String tableName,String column, Object arg) {
        return null;
    }

    @Override
    public String update(String tableName,String chatId, String arg, String value) {
        String query = String.format(UPDATE.getQuery(), tableName, arg, value, CHAT_ID, chatId);
        try {
            ConnectionDb.executeUpdate(query);
            return OK.getStatus();
        } catch (SQLException e) {
            return FAIL.getStatus();
        }
    }

    @Override
    public String delete(String tableName,String id) {
        String query = String.format(DELETE.getQuery(), tableName, CHAT_ID, id);
        try {
            ConnectionDb.executeUpdate(query);
            return OK.getStatus();
        } catch (SQLException e) {
            return FAIL.getStatus();
        }
    }

    @Override
    public Integer count(String tableName) {
        String query = String.format(COUNT.getQuery(), tableName);
        ResultSet res = ConnectionDb.executeQuery(query);
        try {
            while (res.next()){
                return  res.getInt("total");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public String addTable(String name) {
        return null;
    }

    @Override
    public void dropTable(String tableName) {

    }
}
