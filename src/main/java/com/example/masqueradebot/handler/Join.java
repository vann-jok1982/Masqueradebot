package com.example.masqueradebot.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
@Component
public class Join implements CommandHandler{
    @Override
    public SendMessage handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        return createSendMessage(chatId, "логика присоединения к игре ");
    }

    private SendMessage createSendMessage(Long chatId, String text) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
    }
}