package com.enterprise.myshnev.telegrambot.scheduler.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class SuperAdminUtils {
    private static Logger LOGGER = LogManager.getLogger(SuperAdminUtils.class);
    public static String TIME_OF_NOTIFICATION;
    private static Properties properties;
    private static String path;
    private static SuperAdminUtils superAdminUtils;

    public SuperAdminUtils() {
        path = System.getProperty("user.dir") + File.separator + "config.properties";
        properties = new Properties();
        TIME_OF_NOTIFICATION = getTimeNotificationFromFileConfig();
    }

    public  String getIdSuperAdminFromFileConfig() {
        try {
            File file = ResourceUtils.getFile(path);
            InputStream in = new FileInputStream(file);
            assert properties != null;
            properties.load(in);
            in.close();
            return properties.getProperty("superAdmin.userId");
        } catch (IOException e) {
            LOGGER.info(e.getMessage());
        }
        return null;
    }

    public  String getTimeNotificationFromFileConfig() {
        try {
            assert path != null;
            File file = ResourceUtils.getFile(path);
            InputStream in = new FileInputStream(file);
            assert properties != null;
            properties.load(in);
            in.close();
            return properties.getProperty("timeOfNotification");
        } catch (IOException e) {
            LOGGER.info(e.getMessage());
        }
        return null;
    }

    public  String getBotConfigFromFile(String param) {
        try {
            File file = ResourceUtils.getFile(path);
            InputStream in = new FileInputStream(file);
            assert properties != null;
            properties.load(in);
            in.close();
        } catch (IOException e) {
            LOGGER.info(e.getMessage());
        }
        return properties.getProperty(param);
    }

    public  Integer getDEFAULT_MAX_COUNTER() {
        try {
            File file = ResourceUtils.getFile(path);
            InputStream in = new FileInputStream(file);
            assert properties != null;
            properties.load(in);
            in.close();
            String defaultMaxCounter = properties.getProperty("defaultMaxCounter");
            return Integer.parseInt(defaultMaxCounter);
        } catch (IOException e) {
            LOGGER.info(e.getMessage());
        }
        return null;
    }

    public static SuperAdminUtils getInstance() {
        if (superAdminUtils == null) {
            superAdminUtils = new SuperAdminUtils();
        }
        return superAdminUtils;
    }
}
