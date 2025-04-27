package com.example.masqueradebot.keyboard;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Component
public class KeyboardFactory {

    private final KeyboardStart keyboardStart;
    private final KeyboardInGame keyboardInGame;
    private final KeyboardNotInGame keyboardNotInGame;
    private final KeyboardNewUser keyboardNewUser;

    public KeyboardFactory(KeyboardStart keyboardStart, KeyboardInGame keyboardInGame, KeyboardNotInGame keyboardNotInGame, KeyboardNewUser keyboardNewUser) {
        this.keyboardStart = keyboardStart;
        this.keyboardInGame = keyboardInGame;
        this.keyboardNotInGame = keyboardNotInGame;
        this.keyboardNewUser = keyboardNewUser;
    }

    public ReplyKeyboardMarkup getKeyboard(Long chatId) {
        // Логика определения состояния пользователя (пример)
//        if (isNewUser(chatId)) {
//            return keyboardNewUser.getReplyKeyboardMarkup();
//        } else if (isInGame(chatId)) {
//            return keyboardInGame.getReplyKeyboardMarkup();
//        } else {
//            return keyboardNotInGame.getReplyKeyboardMarkup();
        return keyboardStart.getReplyKeyboardMarkup();
//        }
    }

    private boolean isNewUser(Long chatId) {
        // Здесь ваша логика проверки, является ли пользователь новым
        return false; // Заглушка
    }

    private boolean isInGame(Long chatId) {
        // Здесь ваша логика проверки, состоит ли пользователь в игре
        return false; // Заглушка
    }
}