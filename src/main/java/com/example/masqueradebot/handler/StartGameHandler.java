package com.example.masqueradebot.handler;

import com.example.masqueradebot.config.LongPollingBotConfig;
import com.example.masqueradebot.dataBot.enam.GameStatus;
import com.example.masqueradebot.dataBot.model.Game;
import com.example.masqueradebot.dataBot.model.Player;
import com.example.masqueradebot.dataBot.model.User;
import com.example.masqueradebot.dataBot.servis.GameService;
import com.example.masqueradebot.dataBot.servis.PlayerService;
import com.example.masqueradebot.dataBot.servis.UserServis;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;
import java.util.List;

@Component
public class StartGameHandler implements CommandHandler {

    private final GameService gameService;
    private final PlayerService playerService;
    private final UserServis userServis;
    private final LongPollingBotConfig longPollingBotConfig;

    public StartGameHandler(GameService gameService, PlayerService playerService, UserServis userServis, @Lazy LongPollingBotConfig longPollingBotConfig) {
        this.gameService = gameService;
        this.playerService = playerService;
        this.userServis = userServis;
        this.longPollingBotConfig = longPollingBotConfig;
    }

    @Override
    @Transactional
    public SendMessage handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        User user = userServis.FindBiId(chatId);

        Game game = gameService.findActiveGameByUser(user);

        if (game == null) {
            return createSendMessage(chatId, "Вы не являетесь создателем активной игры.");
        }

        if (!game.getCreator().equals(user)) {
            return createSendMessage(chatId, "Только создатель игры может её начать.");
        }

        if (game.getPlayers().size() < 2) {
            return createSendMessage(chatId, "Для начала игры необходимо минимум 2 игрока.");
        }

        game.setStatus(GameStatus.IN_PROGRESS); // Или другой статус
        gameService.saveGame(game); // Сохраняем изменения

        // Инициализация игры
        List<Player> players = game.getPlayers();
        Collections.shuffle(players); // Случайный порядок хода
        game.setPlayers(players); // Сохраняем порядок хода
        gameService.saveGame(game); // Сохраняем изменения

        // Определяем, кто ходит первым
        Player firstPlayer = players.get(0);
        game.setCurrentPlayerTurn(0); // Устанавливаем текущего игрока
        gameService.saveGame(game); // Сохраняем изменения


        // Отправляем уведомления всем игрокам :
        for (Player player : game.getPlayers()) {
            User playerUser = player.getUser();
            SendMessage notification = createSendMessage(playerUser.getTelegramId(), createMessageAllPlayers(game));
            // Отправьте сообщение игроку (используйте ваш механизм отправки сообщений)
            try {
                longPollingBotConfig.execute(notification);
            } catch (TelegramApiException e) {
                System.out.println("Error sending message: " + e.getMessage());
            }

        }
        // Отправляем уведомление первому игроку
        String firstPlayerMessage = createFirstPlayerMessage();
        SendMessage firstPlayerNotification = createSendMessage(firstPlayer.getUser().getTelegramId(), firstPlayerMessage);
        try {
            longPollingBotConfig.execute(firstPlayerNotification);
            System.out.println("Sent first turn notification to user {}"+ firstPlayer.getUser().getTelegramId());
        } catch (TelegramApiException e) {
            System.out.println("Error sending first turn notification to user {}: {}"+ firstPlayer.getUser().getTelegramId()+ e.getMessage()+ e);
        }

        return createSendMessage(chatId, "");
    }

    private SendMessage createSendMessage(Long chatId, String text) {
        return SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .build();
    }

    private String createMessageAllPlayers(Game game) {
        StringBuilder sb = new StringBuilder();
        sb.append("Игра началась ! \n");
        sb.append("Вот все игроки : \n");
        for (Player player : game.getPlayers()) {
            sb.append(player.getNickname()).append("\n");
        }
        return sb.toString();
    }
    private String createFirstPlayerMessage(){
        StringBuilder sb = new StringBuilder();
        sb.append("Ваш ход! \n");
        sb.append("Введи  /move/код игры/псевдоним игрока/вопрос \n");
        sb.append("например  /move/1234/котик/ты ходишь в музыкальную школу ? ");
        return sb.toString();
    }
}