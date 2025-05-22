package com.example.masqueradebot.handler;

import com.example.masqueradebot.dataBot.model.Game;
import com.example.masqueradebot.dataBot.model.Player;
import com.example.masqueradebot.dataBot.model.User;
import com.example.masqueradebot.dataBot.servis.GameService;
import com.example.masqueradebot.dataBot.servis.PlayerService;
import com.example.masqueradebot.dataBot.servis.UserServis;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
@Component
@Transactional
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
            // Получаем текст сообщения
            String messageText = update.getMessage().getText();

            // Проверяем, начинается ли сообщение с "/create:"
            if (!messageText.startsWith("/create:")) {
                return createSendMessage(chatId, "Неверный формат команды. Используйте: /create:свой псевдоним");
            }

            // Извлекаем псевдоним (начиная с 8-го символа, как и раньше)
            String nickname = messageText.substring(8);

            // Проверяем, что псевдоним не пустой
            if (nickname.trim().isEmpty()) {
                return createSendMessage(chatId, "Псевдоним не может быть пустым.");
            }
            // Проверяем, что длина псевдонима в пределах допустимого
            if (nickname.length() < 3 || nickname.length() > 20) {
                return createSendMessage(chatId, "Длина псевдонима должна быть от 3 до 20 символов.");
            }

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
            player.setNickname(nickname);
            player.setRealName(user.getUsername());

            playerService.savePlayer(player);

            // Добавляем игрока в игру *через объект Game*
            gameService.addPlayerToGame(newGame, player);

            // Отправляем пользователю сообщение с информацией о созданной игре
            return createSendMessage(chatId, "Игра создана , код игры : "+ newGame.getGameCode()+
                    "      \n не забудь добавиться в общий чат ,как все решат что готовы жмите /start ");
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