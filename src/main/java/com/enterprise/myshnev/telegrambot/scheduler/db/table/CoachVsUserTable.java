package com.enterprise.myshnev.telegrambot.scheduler.db.table;

import com.enterprise.myshnev.telegrambot.scheduler.db.ConnectionDb;
import com.enterprise.myshnev.telegrambot.scheduler.db.CrudDb;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.CoachVsUserJoin;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.TelegramUser;
import org.sqlite.SQLiteConfig;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.enterprise.myshnev.telegrambot.scheduler.db.CommandQuery.INSERT_INTO;
import static com.enterprise.myshnev.telegrambot.scheduler.db.CommandQuery.SELECT_ALL;
import static com.enterprise.myshnev.telegrambot.scheduler.db.DbStatusResponse.*;


public class CoachVsUserTable implements CrudDb<CoachVsUserJoin> {
    private static final String TABLE = "Coach_x_User";
    private static final String COACH_ID = "coach_id";
    private static final String USER_ID = "user_id";
    private SQLiteConfig config;

    public CoachVsUserTable() {
        config = new SQLiteConfig();
        config.setSharedCache(true);
    }

    @Override
    public String insertIntoTable(CoachVsUserJoin join) {
        boolean isExist = false;
        List<CoachVsUserJoin> listJoin = findAll();
        if (listJoin != null) {
            isExist = listJoin.stream().anyMatch(j -> (join.getCoachId().equals(j.getCoachId())) && (join.getUserId().equals(j.getUserId())));
        }
        if (isExist) {
            return EXIST.getStatus();
        }
        String query = String.format(INSERT_INTO.getQuery(), TABLE, COACH_ID + "," + USER_ID, join);
        try {
            ConnectionDb.executeUpdate(query, config);
            return OK.getStatus();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return EXIST.getStatus();
    }

    @Override
    public List findAll() {
        try {
            String query = String.format(SELECT_ALL.getQuery(), TABLE);
            ResultSet res = ConnectionDb.executeQuery(query, config);
            List<TelegramUser> list = new ArrayList<>();
            while (res.next()) {
                TelegramUser user = new TelegramUser();
                user.setChatId(res.getString(COACH_ID));
                user.setFirstName(res.getString(USER_ID));
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
    public Optional findById(String id) {
        return Optional.empty();
    }

    @Override
    public String update(String id, String arg, String value) {
        return null;
    }

    @Override
    public String delete(String id) {
        return null;
    }

    @Override
    public Integer count() {
        return null;
    }
}
