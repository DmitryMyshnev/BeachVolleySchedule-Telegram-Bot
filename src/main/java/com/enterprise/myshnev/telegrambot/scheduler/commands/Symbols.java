package com.enterprise.myshnev.telegrambot.scheduler.commands;

public class Symbols {
    public static String getSymbol(Integer num){
        return switch (num) {
            case 0 -> "0️⃣";
            case 1 -> "1️⃣";
            case 2 -> "2️⃣";
            case 3 -> "3️⃣";
            case 4 -> "4️⃣";
            case 5 -> "5️⃣";
            case 6 -> "6️⃣";
            case 7 -> "7️⃣";
            case 8 -> "8️⃣";
            case 9 -> "9️⃣";
            case 10 -> " \uD83D\uDD1F";
            default -> null;
        };
    }
}
