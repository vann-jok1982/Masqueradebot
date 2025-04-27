package com.example.masqueradebot.handler;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class New_game implements CommandHandler{
    @Override
    public SendMessage handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        return createSendMessage(chatId, "Придумайте себе псевдоним и отправьте мне /create:свой псевдоним , например так /create:Котик , без пробелов ");
    }

    private SendMessage createSendMessage(Long chatId, String text) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
    }
}