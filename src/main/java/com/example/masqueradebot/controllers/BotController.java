package com.example.masqueradebot.controllers;

import com.example.masqueradebot.config.LongPollingBotConfig;
import com.example.masqueradebot.config.WebHookConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@RequiredArgsConstructor // lombok
public class BotController {
    private final WebHookConfig webHookConfig;
    private final LongPollingBotConfig longPollingBotConfig;

    @PostMapping("/")
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return webHookConfig.onWebhookUpdateReceived(update);
    }

//    @PostMapping("/")
//    public void onUpdateReceived(@RequestBody Update update) {
//        longPollingBotConfig.onUpdateReceived(update);
//    }

}
