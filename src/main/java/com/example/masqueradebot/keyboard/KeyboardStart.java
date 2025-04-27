package com.example.masqueradebot.keyboard;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;

@Component
public class KeyboardStart {

    public  ReplyKeyboardMarkup getReplyKeyboardMarkup() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();//создаём клавиатуру
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        ArrayList<KeyboardRow> keyboardRows = new ArrayList<>(); //ряды в клаве
        KeyboardRow keyboardRow1 = new KeyboardRow();  //1й ряд
        keyboardRow1.add(new KeyboardButton("/правила"));
        keyboardRow1.add(new KeyboardButton("/начать новую игру"));

        KeyboardRow keyboardRow2 = new KeyboardRow();  //2й ряд
        keyboardRow2.add(new KeyboardButton("/присоединиться к игре"));

        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }
}
