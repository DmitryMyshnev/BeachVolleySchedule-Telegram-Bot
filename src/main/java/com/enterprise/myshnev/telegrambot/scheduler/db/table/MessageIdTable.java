package com.enterprise.myshnev.telegrambot.scheduler.db.table;

import com.enterprise.myshnev.telegrambot.scheduler.db.ConnectionDb;
import com.enterprise.myshnev.telegrambot.scheduler.db.CrudDb;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.MessageId;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.TelegramUser;
import com.enterprise.myshnev.telegrambot.scheduler.repository.entity.Workouts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.enterprise.myshnev.telegrambot.scheduler.db.CommandQuery.*;
import static com.enterprise.myshnev.telegrambot.scheduler.db.DbStatusResponse.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MessageIdTable implements CrudDb<MessageId> {
    private static final String ID = "id";
    private static final String MESSAGE_ID = "message_id";
    private static final String CHAT_ID = "chat_id";
    private static final String TIME = "time";
    private static final String DAY_OF_WEEK = "day_of_week";
    public static Logger LOGGER = LogManager.getLogger(MessageIdTable.class);
    @Override
    public String addTable(String tableName) {
        return null;
    }

    @Override
    public String insertIntoTable(String tableName, MessageId messageIdObj) {

        String query = String.format(INSERT_INTO.getQuery(), tableName,
                MESSAGE_ID + "," +
                        CHAT_ID + ", " +
                        TIME + ", " +
                        DAY_OF_WEEK,
                 messageIdObj);
        try {
            ConnectionDb.executeUpdate(query);
            return OK.getStatus();
        } catch (SQLException e) {
            e.printStackTrace();
            return FAIL.getStatus();
        }
    }

    @Override
    public List<MessageId> findAll(String tableName) {
        try {
            String query = String.format(SELECT_ALL.getQuery(), tableName);
            ResultSet res = ConnectionDb.executeQuery(query);
            List<MessageId> list = new ArrayList<>();
            while (res.next()) {
                MessageId messageIdObj = new MessageId();
                messageIdObj.setId(res.getInt(ID));
                messageIdObj.setMessageId(res.getInt(MESSAGE_ID));
                messageIdObj.setChatId(res.getString(CHAT_ID));
                messageIdObj.setTime(res.getString(TIME));
                messageIdObj.setDayOfWeek(res.getString(DAY_OF_WEEK));
                list.add(messageIdObj);
            }
            res.getStatement().close();
            res.close();
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public Optional<MessageId> findById(String tableName, String messasgeId) {
        try {
            String query = String.format(SELECT_WHERE.getQuery(), tableName, MESSAGE_ID, messasgeId);
            ResultSet res = ConnectionDb.executeQuery(query);
            if (res.next()) {
                MessageId messageId = new MessageId();
                messageId.setId(res.getInt(ID));
                messageId.setMessageId(res.getInt(MESSAGE_ID));
                messageId.setChatId(res.getString(CHAT_ID));
                messageId.setTime(res.getString(TIME));
                messageId.setDayOfWeek(res.getString(DAY_OF_WEEK));
                res.getStatement().close();
                res.close();
                return Optional.of(messageId);
            }
            return Optional.empty();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            LOGGER.error(e.getSQLState());
            return Optional.empty();
        }
    }

    @Override
    public List<MessageId> findBy(String tableName, String column, Object arg) {
        List<MessageId> list = new ArrayList<>();
        try {
            String query = String.format(SELECT_WHERE.getQuery(),tableName,column,arg);
            ResultSet res = ConnectionDb.executeQuery(query);
            while (res.next()) {
                MessageId messageId = new MessageId();
                messageId.setId(res.getInt(ID));
                messageId.setMessageId(res.getInt(MESSAGE_ID));
                messageId.setChatId(res.getString(CHAT_ID));
                messageId.setTime(res.getString(TIME));
                messageId.setDayOfWeek(res.getString(DAY_OF_WEEK));
                list.add(messageId);
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
    public String update(String tableName, String chatId, String arg, String value) {
        return null;
    }

    @Override
    public String delete(String tableName, String id) {
        String query = String.format(DELETE.getQuery(), tableName, MESSAGE_ID, id);
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
            count = Objects.requireNonNull(res).getInt("total");
            res.getStatement().close();
            res.close();
            return count;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void dropTable(String tableName) {

    }
}
