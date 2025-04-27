package com.example.masqueradebot.handler;

import com.example.masqueradebot.dataBot.model.Game;
import com.example.masqueradebot.dataBot.model.Player;
import com.example.masqueradebot.dataBot.model.User;
import com.example.masqueradebot.dataBot.servis.GameService;
import com.example.masqueradebot.dataBot.servis.PlayerService;
import com.example.masqueradebot.dataBot.servis.UserServis;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
@Component
public class New_game_create implements CommandHandler{
    UserServis userServis;
    GameService gameService;
    PlayerService playerService;

    public New_game_create(UserServis userServis, GameService gameService, PlayerService playerService) {
        this.userServis = userServis;
        this.gameService = gameService;
        this.playerService = playerService;
    }

    @Override
    public SendMessage handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        User user = userServis.FindBiId(chatId);

        if (user == null) {
            return createSendMessage(chatId, "Не удалось найти пользователя.");
        }
        try {
            // Проверяем, есть ли у пользователя активная игра
            Game activeGame = gameService.findActiveGameByUser(user);

            // Если есть активная игра, удаляем её
            if (activeGame != null) {
                gameService.deleteGame(activeGame);
                // Важно: убедитесь, что каскадное удаление настроено правильно,
                // чтобы все связанные данные (игроки, вопросы и т.д.) были удалены
            }

            // Создаем новую игру (можно передавать параметры, например, максимальное количество игроков)

            Game newGame = gameService.createNewGame(5,user); // Максимальное количество игроков = 5 Передаем user

            // Добавляем пользователя, отправившего команду, в качестве первого игрока в созданную игру
            Player player=new Player();
            player.setUser(user);
            player.setGame(newGame);
            player.setNickname(update.getMessage().getText().substring(8));
            playerService.savePlayer(player);

            // Добавляем игрока в игру *через объект Game*
            gameService.addPlayerToGame(newGame, player);

            // Отправляем пользователю сообщение с информацией о созданной игре
            return createSendMessage(chatId, "Игра создана , код игры : "+ newGame.getGameCode());
        } catch (Exception e) {
            // Обрабатываем исключения
            return createSendMessage(chatId, "Произошла ошибка при создании игры.");
        }
    }

    private SendMessage createSendMessage(Long chatId, String text) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
    }
}