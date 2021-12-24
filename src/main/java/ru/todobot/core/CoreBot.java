package ru.todobot.core;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.todobot.service.SendMessageOperationService;
import ru.todobot.store.HashMapStore;

import java.time.LocalDate;
import java.util.Locale;

import static ru.todobot.constants.VarConstant.*;

public class CoreBot extends TelegramLongPollingBot {
    private SendMessageOperationService sendMessageOperationService = new SendMessageOperationService();
    private HashMapStore store = new HashMapStore();
    private boolean isStartPlanning = false;
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()){
            switch (update.getMessage().getText()){
                case START:
                    executeMessage(sendMessageOperationService.createGreetingInformation(update));
                    executeMessage(sendMessageOperationService.createInstructionMessage(update));
                    break;

                case START_PLANNING:
                    isStartPlanning = true;
                    executeMessage(sendMessageOperationService.createPlanningMessage(update));
                    break;

                case END_PLANNING:
                    isStartPlanning = false;
                    executeMessage(sendMessageOperationService.createEndPlanningMessage(update));
                    break;
                case SHOW_DEALS:
                    if (!isStartPlanning){
                    executeMessage(sendMessageOperationService.createSimpleMessage(update,store.selectAll(LocalDate.now())));
                    executeMessage(sendMessageOperationService.createRemoveMessage(update));
                    }
                    break;
                default:
                    if(isStartPlanning) {
                        store.save(LocalDate.now(), update.getMessage().getText());
                    }
            }
        }
        if (update.hasCallbackQuery()){
            String instruction = "Бот помогает сформировать дела на день.\n\n" +
                    "Чтобы добавить дело нажмите на кнопку \"Начать планирование," +
                    " присылайте дела отдельными сообщениям.\n\nПо завершению нажмите на кнопку \"Закончить планирование.\"\n\n" +
                    "Кнопка \"Показать дела\", поможет вам увидеть уже существующие дела.";
            String callDate = update.getCallbackQuery().getData();
            switch (callDate){
                case YES:
                    EditMessageText text = sendMessageOperationService.createEditMessage(update,instruction);
                    executeMessage(text);
            }

        }
    }

    @Override
    public String getBotUsername() {
        return "ToDo_Shonin_bot";
    }

    @Override
    public String getBotToken() {
        return "2132863700:AAFZRRL2CCihhJdVGzYkPfqfKF8uUuKbO_k";
    }

    private <T extends BotApiMethod> void executeMessage(T sendMessage){
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
