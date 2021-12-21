package com.enterprise.myshnev.telegrambot.scheduler.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SuperAdminUtils {
    private static Logger LOGGER = LogManager.getLogger(SuperAdminUtils.class);
    public static String TIME_OF_NOTIFICATION = getTimeNotificationFromFileConfig();

    public static String getIdSuperAdminFromFileConfig() {
        Properties properties = new Properties();
        try {
            String path = System.getProperty("user.dir") + File.separator + "config.properties";
            File file = ResourceUtils.getFile(path);
            InputStream in = new FileInputStream(file);
            properties.load(in);
            in.close();
        } catch (IOException e) {
            LOGGER.info(e.getMessage());
        }
        return properties.getProperty("superAdmin.userId");
    }

    public static String getTimeNotificationFromFileConfig() {
        Properties properties = new Properties();
        try {
            String path = System.getProperty("user.dir") + File.separator + "config.properties";
            File file = ResourceUtils.getFile(path);
            InputStream in = new FileInputStream(file);
            properties.load(in);
            in.close();
        } catch (IOException e) {
            LOGGER.info(e.getMessage());
        }
        TIME_OF_NOTIFICATION = properties.getProperty("timeOfNotification");
        return TIME_OF_NOTIFICATION;
    }

    public static String getBotConfigFromFile(String param) {
        Properties properties = new Properties();
        try {
            String path = System.getProperty("user.dir") + File.separator + "config.properties";
            File file = ResourceUtils.getFile(path);
            InputStream in = new FileInputStream(file);
            properties.load(in);
            in.close();
        } catch (IOException e) {
            LOGGER.info(e.getMessage());
        }
        String p = properties.getProperty(param);
        return properties.getProperty(param);
    }
}
