package com.enterprise.myshnev.telegrambot.scheduler.db.table;

import com.enterprise.myshnev.telegrambot.scheduler.db.ConnectionDb;
import com.enterprise.myshnev.telegrambot.scheduler.db.CrudDb;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.NewWorkout;
import org.sqlite.SQLiteConfig;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.enterprise.myshnev.telegrambot.scheduler.db.CommandQuery.*;
import static com.enterprise.myshnev.telegrambot.scheduler.db.DbStatusResponse.*;

public class NewWorkoutTable implements CrudDb<NewWorkout> {
    private static String TABLE = "New_workout";
    private static final String USER_ID = "user_id";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String RESERVE = "reserve";
    private static final String WEEK_OF_DAY = "week_of_day";
    private static final String TIME = "time";


    public NewWorkoutTable() {

    }

    public NewWorkoutTable(String TABLE) {
        this.TABLE = TABLE;
    }

    @Override
    public String addTable(String name) {
        String query = String.format(CREATE_TABLE.getQuery(), name,
                USER_ID + " TEXT, " +
                        FIRST_NAME + " TEXT, " +
                        LAST_NAME + " TEXT, " +
                        RESERVE + " INTEGER DEFAULT 0",
                USER_ID);
        try {
            ConnectionDb.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return FAIL.getStatus();
        }
        return name;
    }

    @Override
    public String insertIntoTable(String tableName, NewWorkout workout) {
        if (findById(tableName,workout.getUserId()).isEmpty()) {
            String query = String.format(INSERT_INTO.getQuery(), tableName,
                    USER_ID + ","
                            + FIRST_NAME + ","
                            + LAST_NAME + ","
                            + RESERVE,workout );
            try {
                ConnectionDb.executeUpdate(query);
            } catch (SQLException e) {
                e.printStackTrace();
                return e.getSQLState();
            }
            return OK.getStatus();
        } else
            return EXIST.getStatus();
    }

    @Override
    public List<NewWorkout> findAll(String tableName) {
        try {
            String query = String.format(SELECT_ALL.getQuery(), tableName);
            ResultSet res = ConnectionDb.executeQuery(query);
            List<NewWorkout> list = new ArrayList<>();
            while (res.next()) {
                NewWorkout workout = new NewWorkout();
                workout.setUserId(res.getString(USER_ID));
                workout.setFirstName(res.getString(FIRST_NAME));
                workout.setLastName(res.getString(LAST_NAME));
                workout.setReserve(res.getBoolean(RESERVE));
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
    public Optional<NewWorkout> findById(String tableName,String id) {
        try {
            String query = String.format(SELECT_FROM.getQuery(), tableName, USER_ID, id);
            ResultSet res = ConnectionDb.executeQuery(query);
            if (res.next()) {
                NewWorkout workout = new NewWorkout();
                workout.setUserId(res.getString(USER_ID));
                workout.setFirstName(res.getString(FIRST_NAME));
                workout.setLastName(res.getString(LAST_NAME));
                workout.setReserve(res.getBoolean(RESERVE));
                res.getStatement().close();
                res.close();
                return Optional.of(workout);
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<NewWorkout> findBy(String tableName, String column, Object arg) {

        return null;
    }

    @Override
    public String update(String tableName,String chatId, String arg, String value) {
        String query = String.format(UPDATE.getQuery(), tableName, arg, value, USER_ID, chatId);
        try {
            ConnectionDb.executeUpdate(query);
            return OK.getStatus();
        } catch (SQLException e) {
            return FAIL.getStatus();
        }
    }

    @Override
    public String delete(String tableName,String id) {
        String query = String.format(DELETE.getQuery(), tableName, USER_ID, id);
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
        int count;
        try {
            count = res.getInt("total");
            res.getStatement().close();
            res.close();
            return count;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
