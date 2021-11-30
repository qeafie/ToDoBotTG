package ru.todobot.core;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CoreBot extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("test");
    }

    @Override
    public String getBotUsername() {
        return "ToDo_Shonin_bot";
    }

    @Override
    public String getBotToken() {
        return "2132863700:AAFZRRL2CCihhJdVGzYkPfqfKF8uUuKbO_k";
    }


}
