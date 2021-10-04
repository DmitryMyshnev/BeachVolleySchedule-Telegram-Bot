package com.enterprise.myshnev.telegrambot.scheduler.db.table;

import com.enterprise.myshnev.telegrambot.scheduler.db.ConnectionDb;
import com.enterprise.myshnev.telegrambot.scheduler.db.CrudDb;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.TelegramUser;
import static com.enterprise.myshnev.telegrambot.scheduler.db.DbStatusResponse.*;

import org.sqlite.SQLiteConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static com.enterprise.myshnev.telegrambot.scheduler.db.CommandQuery.*;
public class UserTable implements CrudDb<TelegramUser> {
    private static final String TABLE = "Users";
    private static final String CHAT_ID = "chat_id";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String ADMIN = "admin";
    private static final String COACH = "coach";

    private SQLiteConfig config;

    public UserTable() {
        config = new SQLiteConfig();
        config.setSharedCache(true);
    }

    @Override
    public String insertIntoTable(TelegramUser telegramUser) {
        if (findById(telegramUser.getChatId()).isEmpty()) {
            String query = String.format(INSERT_INTO.getQuery(), TABLE, CHAT_ID + "," + FIRST_NAME + "," + LAST_NAME + "," + ADMIN + "," + COACH, telegramUser);
            try {
                ConnectionDb.executeUpdate(query, config);
            } catch (SQLException e) {
                e.printStackTrace();
                return e.getSQLState();
            }
            return OK.getStatus();
        } else
            return EXIST.getStatus();
    }

    @Override
    public Optional<TelegramUser> findById(String id) {
        try {
            String query = String.format(SELECT_FROM.getQuery(), TABLE, CHAT_ID, id);
            ResultSet res = ConnectionDb.executeQuery(query, config);
            if (res.next()) {
                TelegramUser user = new TelegramUser();
                user.setChatId(res.getString(CHAT_ID));
                user.setFirstName(res.getString(FIRST_NAME));
                user.setLastName(res.getString(LAST_NAME));
                user.setAdmin(res.getBoolean(ADMIN));
                user.setCoach(res.getBoolean(COACH));
                res.getStatement().close();
                res.getStatement().getConnection().close();
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
    public String update(String id, String arg, String value) {
        String query = String.format(UPDATE.getQuery(), TABLE, arg, value, CHAT_ID, id);
        try {
            ConnectionDb.executeUpdate(query, config);
            return OK.getStatus();
        } catch (SQLException e) {
            e.printStackTrace();
            return e.getSQLState();
        }
    }

    @Override
    public String delete(String id) {
        String query = String.format(DELETE.getQuery(), TABLE, CHAT_ID, id);
        try {
            ConnectionDb.executeUpdate(query, config);
            return OK.getStatus();
        } catch (SQLException e) {
            e.printStackTrace();
            return e.getSQLState();
        }
    }

    @Override
    public List<TelegramUser> findAll() {
        try {
            String query = String.format(SELECT_ALL.getQuery(), TABLE);
            ResultSet res = ConnectionDb.executeQuery(query, config);
            List<TelegramUser> list = new ArrayList<>();
            while (res.next()) {
                TelegramUser user = new TelegramUser();
                user.setChatId(res.getString(CHAT_ID));
                user.setFirstName(res.getString(FIRST_NAME));
                user.setLastName(res.getString(LAST_NAME));
                user.setAdmin(res.getBoolean(ADMIN));
                user.setCoach(res.getBoolean(COACH));
                list.add(user);
            }
            res.getStatement().close();
            res.getStatement().getConnection().close();
            res.close();
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Integer count() {
        String query = String.format(COUNT.getQuery(), TABLE);
        ResultSet res = ConnectionDb.executeQuery(query, config);
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
}
