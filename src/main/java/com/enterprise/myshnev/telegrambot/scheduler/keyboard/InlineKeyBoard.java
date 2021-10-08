package com.enterprise.myshnev.telegrambot.scheduler.keyboard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class InlineKeyBoard {
     InlineKeyboardButton button;
     List<InlineKeyboardButton> row;
     List<List<InlineKeyboardButton>> rowList;
     InlineKeyboardMarkup keyboard;

    public InlineKeyBoard() {

        rowList = new ArrayList<>();
        keyboard = new InlineKeyboardMarkup();
    }

    public  InlineKeyboardMarkup addButton(String text, String callback) {
        row = new ArrayList<>();
        button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callback);
        row.add(button);
        rowList.add(row);
        keyboard.setKeyboard(rowList);
        return keyboard;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        InlineKeyboardButton button;
        List<InlineKeyboardButton> row;
        List<List<InlineKeyboardButton>> rowList;
        InlineKeyboardMarkup keyboard;

        public Builder() {
            keyboard = new InlineKeyboardMarkup();
            row = new ArrayList<>();
            rowList = new ArrayList<>();
        }

        public Builder add(String text, String callback, boolean newRow) {
            button = new InlineKeyboardButton();
            button.setText(text);
            button.setCallbackData(callback);
            row.add(button);
            if (newRow) {
                rowList.add(row);
                row = new ArrayList<>();
            }
            return this;
        }

        public Builder add(String text, String callback) {
            button = new InlineKeyboardButton();
            button.setText(text);
            button.setCallbackData(callback);
            row.add(button);
            return this;
        }

        public InlineKeyboardMarkup create() {
            rowList.add(row);
            keyboard.setKeyboard(rowList);
            return keyboard;
        }
    }

}
