package com.aesvna;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new BunnyMemoBot());
            System.out.println("BunnyMemoBot запущен! 🐇");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
