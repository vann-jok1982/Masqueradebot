package com.example.masqueradebot.handler;

import com.example.masqueradebot.config.LongPollingBotConfig;
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

import java.util.List;

@Component
public class Join_create implements CommandHandler{
    private final UserServis userServis;
    private final GameService gameService;
    private final PlayerService playerService;
    private final LongPollingBotConfig longPollingBotConfig;

    public Join_create(UserServis userServis, GameService gameService, PlayerService playerService,@Lazy LongPollingBotConfig longPollingBotConfig) {
        this.userServis = userServis;
        this.gameService = gameService;
        this.playerService = playerService;
        this.longPollingBotConfig = longPollingBotConfig;
    }

    @Override
    @Transactional
    public SendMessage handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        User user = userServis.FindBiId(chatId);

        if (user == null) {
            return createSendMessage(chatId, "Не удалось найти пользователя.");
        }

        try {
            // Получаем текст сообщения
            String messageText = update.getMessage().getText();

            // Разбиваем текст сообщения на части по разделителю "/"
            String[] parts = messageText.split("/");

            // Проверяем, что команда имеет правильный формат
            if (parts.length != 4 || !parts[1].equals("join")) {
                return createSendMessage(chatId, "Неверный формат команды. Используйте: /join/код игры/свой псевдоним");
            }

            // Извлекаем код игры и псевдоним
            Long gameCode = Long.parseLong(parts[2]);
            String nickname = parts[3];

            // Находим игру по коду
            Game game = gameService.findGameByCode(gameCode);

            if (game == null) {
                return createSendMessage(chatId, "Игра с кодом " + gameCode + " не найдена.");
            }
            // Проверяем, не превышено ли максимальное количество игроков
            if (game.getPlayers().size() >= game.getMaxPlayers()) {
                return createSendMessage(chatId, "В игре " + gameCode + " достигнуто максимальное количество игроков.");
            }

            // Создаем игрока
            Player findPlayer=playerService.findByPlayer(user);
            System.out.println("findPlayer: "+findPlayer);
            if (findPlayer != null) {

            //    1. Получаем игру, в которой состоит игрок
        Game gamePleer = findPlayer.getGame();

        // 2. Удаляем игрока из списка игроков в игре
        List<Player> players = gamePleer.getPlayers();
        players.remove(findPlayer); // Удаляем игрока из списка

        // 3. Сохраняем изменения в игре
        gameService.saveGame(gamePleer);
                // 4. Удаляем игрока
                playerService.deletePlayer(findPlayer);
            }
            Player player = new Player();
            player.setUser(user);
            player.setGame(game);
            player.setNickname(nickname);
            player.setRealName(user.getUsername());

            // Сохраняем игрока
            playerService.savePlayer(player);

            // Добавляем игрока в игру *через объект Game*
            gameService.addPlayerToGame(game, player);

            //отправляем сообщение создателю игры об присоединении игрока
            try {
                longPollingBotConfig.execute(createSendMessage(game.getCreator().getTelegramId(),"Игрок "+ player.getRealName()+" добавился."));
            } catch (TelegramApiException e) {
                System.out.println("Error sending message: " + e.getMessage());
            }
            // Отправляем пользователю сообщение об успешном присоединении
            return createSendMessage(chatId, "Вы успешно присоединились к игре " + gameCode + " под псевдонимом " + nickname +
                    "    \n не забудь добавиться в общий чат ,как решите что готовы сообщите в общем чате и создатель запустит её");

        } catch (NumberFormatException e) {
            return createSendMessage(chatId, "Неверный формат кода игры. Код игры должен быть числом.");
        } catch (Exception e) {
            // Обрабатываем исключения
            return createSendMessage(chatId, "Произошла ошибка при присоединении к игре.");
        }
    }

    private SendMessage createSendMessage(Long chatId, String text) {
        return SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .build();
    }
}