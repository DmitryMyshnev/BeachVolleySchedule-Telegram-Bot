package com.enterprise.myshnev.telegrambot.scheduler.db.table;

import com.enterprise.myshnev.telegrambot.scheduler.db.ConnectionDb;
import com.enterprise.myshnev.telegrambot.scheduler.db.CrudDb;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.TelegramUser;
import org.sqlite.SQLiteConfig;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.enterprise.myshnev.telegrambot.scheduler.db.CommandQuery.*;
import static com.enterprise.myshnev.telegrambot.scheduler.db.DbStatusResponse.*;

public class CoachTable implements CrudDb<TelegramUser> {
    private static final String TABLE = "Coach";
    private static final String CHAT_ID = "chat_id";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private SQLiteConfig config;

    public CoachTable() {
        config = new SQLiteConfig();
        config.setSharedCache(true);
    }

    @Override
    public String insertIntoTable(TelegramUser telegramUser) {
        if (findById(telegramUser.getChatId()).isEmpty()) {
            String query = String.format(INSERT_INTO.getQuery(), TABLE, CHAT_ID + "," + FIRST_NAME + "," + LAST_NAME, telegramUser.fullName());
            try {
                ConnectionDb.executeUpdate(query, config);
                return OK.getStatus();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return EXIST.getStatus();
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
    public Optional<TelegramUser> findById(String id) {
        try {
            String query = String.format(SELECT_FROM.getQuery(), TABLE,CHAT_ID, id);
            ResultSet res = ConnectionDb.executeQuery(query,config);
            if (res.next()) {
                TelegramUser user = new TelegramUser();
                user.setChatId(res.getString(CHAT_ID));
                user.setFirstName(res.getString(FIRST_NAME));
                user.setLastName(res.getString(LAST_NAME));
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
    public String update(String id, String arg,String value) {
        String query = String.format(UPDATE.getQuery(), TABLE, arg, value, CHAT_ID, id);
        try {
            ConnectionDb.executeUpdate(query, config);
            return OK.getStatus();
        } catch (SQLException e) {
            e.printStackTrace();
            return FAIL.getStatus();
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
            return FAIL.getStatus();
        }
    }
}
