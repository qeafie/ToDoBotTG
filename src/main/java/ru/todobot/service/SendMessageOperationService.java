package ru.todobot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.List;

import static java.lang.Math.toIntExact;
import static ru.todobot.constants.VarConstant.*;
import static java.util.Arrays.asList;

public class SendMessageOperationService {
    private final String GREETING_MESSAGE = "Привет, приступим к планированию";
    private final String PLANNING_MESSAGE = "Вводите дела, после планирования нажмите кнопку \"Закончить планирование\"";
    private final String END_PLANNING_MESSAGE = "Планирование окончено для просмотра нажмите кнопку \"Показать дела\"";
    private final String INSTRUCTIONS = "Хочешь прочесть инструкцию?";
    private final String REMOVE_DEAL = "Хочешь удалить дело?";

    private final ButtonService buttonService = new ButtonService();

    public SendMessage createGreetingInformation(Update update){
       SendMessage message= createSimpleMessage(update, GREETING_MESSAGE);
        ReplyKeyboardMarkup keyboardMarkup = buttonService.setButtons(buttonService.createButtons(
                asList(START_PLANNING, END_PLANNING, SHOW_DEALS)));
        message.setReplyMarkup(keyboardMarkup);
        return message;
    }

    public SendMessage createPlanningMessage(Update update){
        return createSimpleMessage(update, PLANNING_MESSAGE);
    }

    public SendMessage createEndPlanningMessage(Update update){
        return createSimpleMessage(update, END_PLANNING_MESSAGE);
    }

    public SendMessage createSimpleMessage(Update update,String message) {
        SendMessage sendMessage= new SendMessage();
        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        sendMessage.setText(message);
        return sendMessage;
    }

    public SendMessage createSimpleMessage(Update update, List<String> messages) {
        SendMessage sendMessage= new SendMessage();
        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        StringBuilder message = new StringBuilder();
        for(String s:messages){
            message.append(messages.indexOf(s)+1).append(") ").append(s).append("\n");
        }
        sendMessage.setText(message.toString());
        return sendMessage;
    }

    public SendMessage createInstructionMessage(Update update) {
        SendMessage sendMessage = createSimpleMessage(update,INSTRUCTIONS);
        InlineKeyboardMarkup replyKeyboardMarkup =
                buttonService.setInlineKeyboard(buttonService.createInlineButton(YES));
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }

    public EditMessageText createEditMessage(Update update, String instruction) {
        EditMessageText editMessageText = new EditMessageText();
        long mesId = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        editMessageText.setChatId(String.valueOf(chatId));
        editMessageText.setMessageId(toIntExact(mesId));
        editMessageText.setText(instruction);
        return editMessageText;
    }

    public SendMessage createRemoveMessage(Update update) {
        SendMessage sendMessage = createSimpleMessage(update,REMOVE_DEAL);
        InlineKeyboardMarkup replyKeyboardMarkup =
                buttonService.setInlineKeyboard(buttonService.createInlineButton(YES));
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }
}
