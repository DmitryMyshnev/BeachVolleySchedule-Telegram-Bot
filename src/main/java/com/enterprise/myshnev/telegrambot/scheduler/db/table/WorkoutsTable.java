package com.enterprise.myshnev.telegrambot.scheduler.db.table;

import com.enterprise.myshnev.telegrambot.scheduler.db.ConnectionDb;
import com.enterprise.myshnev.telegrambot.scheduler.db.CrudDb;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.Workouts;
import org.sqlite.SQLiteConfig;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.enterprise.myshnev.telegrambot.scheduler.db.CommandQuery.SELECT_ALL;
import static com.enterprise.myshnev.telegrambot.scheduler.db.CommandQuery.SELECT_FROM;

public class WorkoutsTable implements CrudDb<Workouts> {
    private static final String TABLE = "Workout";
    private static final String ID = "id";
    private static final String COACH_ID = "coach_id";
    private static final String WEEK_OF_DAY = "week_of_day";
    private static final String TIME = "time";
    private static final String MAX_COUNT_USER = "max_count_user";

    public WorkoutsTable() {

    }

    @Override
    public String insertIntoTable(String tableName,Workouts type) {
        return null;
    }

    @Override
    public List<Workouts> findBy(String tableName,String column,Object arg) {
        try {
            String query = String.format(SELECT_FROM.getQuery(),tableName,column,arg);
            ResultSet res = ConnectionDb.executeQuery(query);
            List<Workouts> list = new ArrayList<>();
            while (res.next()) {
                Workouts workout = new Workouts();
                workout.setId(res.getInt(ID));
                workout.setCoachId(res.getString(COACH_ID));
                workout.setWeekOfDay(res.getString(WEEK_OF_DAY));
                workout.setTime(res.getString(TIME));
                workout.setMaxCountUser(res.getInt(MAX_COUNT_USER));
                list.add(workout);
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
    public List<Workouts> findAll(String tableName) {
        try {
            String query = String.format(SELECT_ALL.getQuery(), tableName);
            ResultSet res = ConnectionDb.executeQuery(query);
            List<Workouts> list = new ArrayList<>();
            while (res.next()) {
                Workouts workout = new Workouts();
                workout.setId(res.getInt(ID));
                workout.setCoachId(res.getString(COACH_ID));
                workout.setWeekOfDay(res.getString(WEEK_OF_DAY));
                workout.setTime(res.getString(TIME));
                workout.setMaxCountUser(res.getInt(MAX_COUNT_USER));
                list.add(workout);
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
    public Optional<Workouts> findById(String tableName,String column) {
       return Optional.empty();
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
    public String addTable(String name) {
        return null;
    }
}
