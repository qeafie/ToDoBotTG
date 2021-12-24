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
import java.util.List;

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
                    if (isStartPlanning){
                        executeMessage(sendMessageOperationService.createSimpleMessage(update,PLANNING_NOT_END));
                    }else {
                        isStartPlanning = true;
                        executeMessage(sendMessageOperationService.createPlanningMessage(update));
                    }
                    break;

                case END_PLANNING:
                    if (!isStartPlanning){
                        executeMessage(sendMessageOperationService.createSimpleMessage(update,PLANNING_NOT_START));
                    }
                    else {
                        isStartPlanning = false;
                        executeMessage(sendMessageOperationService.createEndPlanningMessage(update));
                    }
                    break;

                case SHOW_DEALS:
                    if (!isStartPlanning){
                        if (!store.isEmpty(LocalDate.now())){
                            executeMessage(sendMessageOperationService.createSimpleMessage(update,store.selectAll(LocalDate.now())));
                            executeMessage(sendMessageOperationService.createRemoveMessages(update));

                        }
                        else executeMessage(sendMessageOperationService.createSimpleMessage(update,LIST_EMPTY ));

                    }
                    else{ executeMessage(sendMessageOperationService.createSimpleMessage(update,PLANNING_NOT_END));
                    }

                    break;

                default:
                    if(isStartPlanning) {
                        store.save(LocalDate.now(), update.getMessage().getText());
                    }
            }
        }
        if (update.hasCallbackQuery()){

            String callDate = update.getCallbackQuery().getData();
            switch (callDate){
                case YES:
                    EditMessageText textINS = sendMessageOperationService.createEditMessage(update, INSTRUCTION);
                    executeMessage(textINS);
                    break;

                case DELETE_ALL:
                    store.deleteAll(LocalDate.now());
                    EditMessageText textDEL = sendMessageOperationService.createEditMessage(update, DELETE_ALL_DONE);
                    executeMessage(textDEL);

                    break;
            }

        }
    }

    @Override
    public String getBotUsername() {
        return "ToDo_Shonin_bot";
    }

    @Override
    public String getBotToken() {
        return "null";
    }

    private <T extends BotApiMethod> void executeMessage(T sendMessage){
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private <T extends BotApiMethod> void executeMessage(List<SendMessage> sendMessages){
        try {
            for (SendMessage sendMessage :sendMessages) {
                execute(sendMessage);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
