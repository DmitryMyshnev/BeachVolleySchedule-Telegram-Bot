package com.enterprise.myshnev.telegrambot.scheduler.db.table;

import com.enterprise.myshnev.telegrambot.scheduler.db.ConnectionDb;
import com.enterprise.myshnev.telegrambot.scheduler.db.CrudDb;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.Workouts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.enterprise.myshnev.telegrambot.scheduler.db.CommandQuery.*;
import static com.enterprise.myshnev.telegrambot.scheduler.db.DbStatusResponse.*;

public class WorkoutsTable implements CrudDb<Workouts> {
    private static final String ID = "id";
    private static final String COACH_ID = "coach_id";
    private static final String WEEK_OF_DAY = "week_of_day";
    private static final String TIME = "time";
    private static final String MAX_COUNT_USER = "max_count_user";
    private static final String IS_ACTIVE = "active";
    public static Logger LOGGER = LogManager.getLogger(WorkoutsTable.class);

    public WorkoutsTable() {

    }

    @Override
    public String insertIntoTable(String tableName,Workouts workouts) {
            String query = String.format(INSERT_INTO.getQuery(), tableName, COACH_ID + "," + WEEK_OF_DAY + "," + TIME + "," + IS_ACTIVE , workouts);
            try {
                ConnectionDb.executeUpdate(query);
            } catch (SQLException e) {
              LOGGER.error(e.getMessage());
              LOGGER.error(e.getSQLState());
            }
            return OK.getStatus();
    }

    @Override
    public Optional<Workouts> findById(String tableName,String id) {
        try {
            String query = String.format(SELECT_WHERE.getQuery(), tableName, ID, id);
            ResultSet res = ConnectionDb.executeQuery(query);
            if (res.next()) {
                Workouts workout = new Workouts();
                workout.setCoachId(res.getString(COACH_ID));
                workout.setDayOfWeek(res.getString(WEEK_OF_DAY));
                workout.setTime(res.getString(TIME));
                workout.setMaxCountUser(res.getInt(MAX_COUNT_USER));
                workout.setActive(res.getBoolean(IS_ACTIVE));
                res.getStatement().close();
                res.close();
                return Optional.of(workout);
            }
            return Optional.empty();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            LOGGER.error(e.getSQLState());
            return Optional.empty();
        }
    }

    @Override
    public List<Workouts> findBy(String tableName,String column,Object arg) {
        List<Workouts> list = new ArrayList<>();
        try {
            String query = String.format(SELECT_WHERE.getQuery(),tableName,column,arg);
            ResultSet res = ConnectionDb.executeQuery(query);
            while (res.next()) {
                Workouts workout = new Workouts();
                workout.setId(res.getInt(ID));
                workout.setCoachId(res.getString(COACH_ID));
                workout.setDayOfWeek(res.getString(WEEK_OF_DAY));
                workout.setTime(res.getString(TIME));
                workout.setMaxCountUser(res.getInt(MAX_COUNT_USER));
                workout.setActive(res.getBoolean(IS_ACTIVE));
                list.add(workout);
            }
            res.getStatement().close();
            res.close();
            return list;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            LOGGER.error(e.getSQLState());
        }
        return list;
    }

    @Override
    public  List<Workouts> findAll(String tableName) {
        List<Workouts> list = new ArrayList<>();
        try {
            String query = String.format(SELECT_ALL.getQuery(), tableName);
            ResultSet res = ConnectionDb.executeQuery(query);
            while (res.next()) {
                Workouts workout = new Workouts();
                workout.setId(res.getInt(ID));
                workout.setCoachId(res.getString(COACH_ID));
                workout.setDayOfWeek(res.getString(WEEK_OF_DAY));
                workout.setTime(res.getString(TIME));
                workout.setMaxCountUser(res.getInt(MAX_COUNT_USER));
                workout.setActive(res.getBoolean(IS_ACTIVE));
                list.add(workout);
            }
            res.getStatement().close();
            res.close();
            return list;
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            LOGGER.error(e.getSQLState());
        }
        return list;
    }

    @Override
    public void dropTable(String tableName) {

    }

    @Override
    public String update(String tableName,String id, String arg, String value) {
        String query = String.format(UPDATE.getQuery(), tableName, arg, value, ID, id);
        try {
            ConnectionDb.executeUpdate(query);
            return OK.getStatus();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            LOGGER.error(e.getSQLState());
            return FAIL.getStatus();
        }
    }

    @Override
    public String delete(String tableName,String id) {
        String query = String.format(DELETE.getQuery(), tableName, ID, id);
        try {
            ConnectionDb.executeUpdate(query);
            return OK.getStatus();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            LOGGER.error(e.getSQLState());
            return FAIL.getStatus();
        }
    }

    @Override
    public Integer count(String tableName) {
        return null;
    }

    @Override
    public String addTable(String name) {
        return null;
    }
}
