package com.example.masqueradebot.config;


import com.example.masqueradebot.botFasade.BotFasade;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
@Slf4j  //логирование
@RequiredArgsConstructor
public class WebHookConfig extends TelegramWebhookBot {
    @Lazy
    private final LongPollingBotConfig longPollingBotConfig;

    private final BotFasade botFasade;
    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) { //метод для возврата сообщений
        SendMessage sendMessage = botFasade.obrabotkaHandleUpdate(update);
        return sendMessage;
    }
    public void execute(SendMessage sendMessage) throws TelegramApiException {
        log.info("Sending message to chatId: " + sendMessage.getChatId());
        longPollingBotConfig.execute(sendMessage);
    }

    @Override
    public String getBotPath() {   //просит строчку из нгрок(url)
        return "https://709536fc764b63b7ed9348589454cbe3.serveo.net";
    }

    @Override
    public String getBotUsername() {  //просит имя бота
        return "/@MasqueradeGameBot";
    }

    @PostConstruct
    public void init() {  //обязательный метод
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(this, SetWebhook.builder().url(getBotPath()).build());
            setWebHook();
        } catch (Exception e) {
        }
    }

    public void setWebHook() throws IOException {
        String token = "7706965489:AAHR6381gywXExGNddCSE_JgDQ-ulpij_YI";
        String url = String.format("https://api.telegram.org/bot%s/setWebhook?url=%s", token, getBotPath());
        log.info(url);
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setRequestMethod("GET");
        log.info(connection.getResponseMessage());
    }
}