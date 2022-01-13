package com.enterprise.myshnev.telegrambot.scheduler.db.table;

import com.enterprise.myshnev.telegrambot.scheduler.db.ConnectionDb;
import com.enterprise.myshnev.telegrambot.scheduler.db.CrudDb;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.Statistic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.enterprise.myshnev.telegrambot.scheduler.db.CommandQuery.INSERT_INTO;
import static com.enterprise.myshnev.telegrambot.scheduler.db.CommandQuery.SELECT_ALL;
import static com.enterprise.myshnev.telegrambot.scheduler.db.DbStatusResponse.OK;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class StatisticTable implements CrudDb<Statistic> {
    private static final String ID = "id";
    private static final String USER_ID = "user_id";
    private static final String USER_NAME = "user_name";
    private static final String WORKOUT = "workout";
    private static final String ACTION = "action";
    private static final String DATE = "date";
    public static Logger LOGGER = LogManager.getLogger(StatisticTable.class);

    @Override
    public String addTable(String tableName) {
        return null;
    }

    @Override
    public String insertIntoTable(String tableName, Statistic stat) {
        String query = String.format(INSERT_INTO.getQuery(), tableName,
                USER_ID + ","
                        + USER_NAME + ","
                        + WORKOUT +","
                        + ACTION + ","
                        + DATE , stat);
        try {
            ConnectionDb.executeUpdate(query);
        } catch (SQLException e) {
            LOGGER.info(e.getMessage());
            LOGGER.info(e.getSQLState());
            return e.getSQLState();
        }
        return OK.getStatus();
}

    @Override
    public List<Statistic> findAll(String tableName) {
        String query = String.format(SELECT_ALL.getQuery(), tableName);
        try ( ResultSet res = ConnectionDb.executeQuery(query)){
            List<Statistic> list = new ArrayList<>();
            while (Objects.requireNonNull(res).next()) {
                Statistic stat = new Statistic();
                stat.setId(res.getInt(ID));
                stat.setUserId(res.getString(USER_ID));
                stat.setUserName(res.getString(USER_NAME));
                stat.setWorkout(res.getString(WORKOUT));
                stat.setAction(res.getString(ACTION));
                stat.setDate(res.getString(DATE));
                list.add(stat);
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
    public Optional<Statistic> findById(String tableName, String id) {
        return Optional.empty();
    }

    @Override
    public List<Statistic> findBy(String tableName, String column, Object arg) {
        return null;
    }

    @Override
    public String update(String tableName, String chatId, String arg, String value) {
        return null;
    }

    @Override
    public String delete(String tableName, String id) {
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
