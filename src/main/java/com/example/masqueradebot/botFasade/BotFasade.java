package com.example.masqueradebot.botFasade;

import com.example.masqueradebot.dataBot.servis.GameService;
import com.example.masqueradebot.dataBot.servis.PlayerService;
import com.example.masqueradebot.dataBot.servis.QuestionService;
import com.example.masqueradebot.dataBot.servis.UserServis;
import com.example.masqueradebot.handler.*;
import com.example.masqueradebot.keyboard.KeyboardFactory;
import com.example.masqueradebot.keyboard.KeyboardStart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@Slf4j
public class BotFasade {

        private final DefaultCommandHandler defaultCommandHandler;
        private final Map<String, CommandHandler> commandHandlers = new HashMap<>();
        private final KeyboardFactory keyboardFactory;

        private final List<CommandHandler> commandHandlerList;

        private final GameService gameService; // Внедрили GameService
        private final QuestionService questionService; // Внедрили QuestionService
        private final PlayerService playerService; // Внедрили PlayerService
        private final UserServis userServis;

        public BotFasade(List<CommandHandler> commandHandlerList, DefaultCommandHandler defaultCommandHandler, KeyboardFactory keyboardFactory, GameService gameService, QuestionService questionService, PlayerService playerService, UserServis userServis) {
            this.defaultCommandHandler = defaultCommandHandler;
            this.keyboardFactory = keyboardFactory;
            this.commandHandlerList = commandHandlerList;
            this.gameService = gameService;
            this.questionService = questionService;
            this.playerService = playerService;
            this.userServis = userServis;
            initializeCommandHandlers();
        }

        private void initializeCommandHandlers() {
            for (CommandHandler commandHandler : commandHandlerList) {
                if (commandHandler instanceof Help) {
                    this.commandHandlers.put("/правила", commandHandler);
                } else if (commandHandler instanceof Join) {
                    this.commandHandlers.put("/присоединиться к игре", commandHandler);
                } else if (commandHandler instanceof New_game) {
                    this.commandHandlers.put("/начать новую игру", commandHandler);
                }else if (commandHandler instanceof New_game_create) {
                    this.commandHandlers.put("/create:", commandHandler);
                } else if (commandHandler instanceof Join_create) {
                    this.commandHandlers.put("/join", commandHandler);
                }
            }
            log.info("Initialized command handlers: {}", this.commandHandlers.keySet());
        }


        public SendMessage obrabotkaHandleUpdate(Update update) {
        log.info("Received update: {}", update);
        String userMessage = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        String username = update.getMessage().getFrom().getUserName();
        if (userServis.FindBiId(chatId) == null) userServis.SaveUser(chatId, username);
        SendMessage sendMessage=null;
        for (Map.Entry<String, CommandHandler> entry : commandHandlers.entrySet()) {
//            log.info("entry.getKey(): {}",entry.getKey());
            if(userMessage.toLowerCase().startsWith(entry.getKey())){
                sendMessage = entry.getValue().handle(update);
                break;
            }
        }
        if(sendMessage == null)
            sendMessage =  defaultCommandHandler.handle(update);
        sendMessage.setReplyMarkup(keyboardFactory.getKeyboard(chatId));
        return sendMessage;
    }
}
