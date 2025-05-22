package com.example.masqueradebot.handler;

import com.example.masqueradebot.config.LongPollingBotConfig;
import com.example.masqueradebot.dataBot.enam.GameStatus;
import com.example.masqueradebot.dataBot.model.Game;
import com.example.masqueradebot.dataBot.model.Player;
import com.example.masqueradebot.dataBot.model.Question;
import com.example.masqueradebot.dataBot.model.User;
import com.example.masqueradebot.dataBot.servis.GameService;
import com.example.masqueradebot.dataBot.servis.PlayerService;
import com.example.masqueradebot.dataBot.servis.QuestionService;
import com.example.masqueradebot.dataBot.servis.UserServis;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class AnswerHHandler implements CommandHandler {
    private final GameService gameService;
    private final PlayerService playerService;
    private final UserServis userServis;
    private final QuestionService questionService;
    private final LongPollingBotConfig longPollingBotConfig;

    public AnswerHHandler(GameService gameService, PlayerService playerService, UserServis userServis, QuestionService questionService, @Lazy LongPollingBotConfig longPollingBotConfig) {
        this.gameService = gameService;
        this.playerService = playerService;
        this.userServis = userServis;
        this.questionService = questionService;
        this.longPollingBotConfig = longPollingBotConfig;
    }

    @Transactional
    @Override
    public SendMessage handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        User user=userServis.FindBiId(chatId);
        if (user == null) {
            return createSendMessage(chatId, "Не удалось найти пользователя.");
        }

        try {
            // Получаем текст сообщения
            String messageText = update.getMessage().getText();

            // Разбиваем текст сообщения на части по разделителю "/"
            String[] parts = messageText.split("/");

            // Проверяем, что команда имеет правильный формат
            if (parts.length != 4 || !parts[1].equals("answer")) {
                return createSendMessage(chatId, "Неверный формат команды. Используйте: /answer/код игры/ваш ответ");
            }

            // Извлекаем код игры и ответ
            Long gameCode = Long.parseLong(parts[2]);
            String answerPars = parts[3];

            // Находим игру по коду
            Game game = gameService.findGameByCode(gameCode);

            if (game == null) {
                return createSendMessage(chatId, "Игра с кодом " + gameCode + " не найдена.");
            }
            if (game.getStatus()!= GameStatus.WAITING_FOR_ANSWER){
                return createSendMessage(chatId, "Вы не можете задавать вопрос сейчас ");
            }
            Player currentPlayerTurn=game.getPlayers().get(game.getCurrentPlayerTurn());// игрок задавший вопрос
            System.out.println("currentPlayerTurn = " + currentPlayerTurn);
            User userTurn=currentPlayerTurn.getUser();//user задавший вопрос

            Long target = game.getLastQuestionAsked().getTarget();// Id отвечающего
            System.out.println("target = " + target);
            if (! user.getTelegramId().equals(target )){
                return createSendMessage(chatId, "Отвечает сейчас другой игрок ");
            }

            // Добавляем ответ
            Question question=game.getLastQuestionAsked();
            question.setAnswerText(answerPars);
            questionService.saveQuestion(question);
            game.setStatus(GameStatus.IN_PROGRESS);
            // устанавливаем следующего игрока и делаем Циклический переход хода:
            game.setCurrentPlayerTurn((game.getCurrentPlayerTurn()+1) % game.getPlayers().size());
            gameService.saveGame(game);


            // Отправляем уведомления всем игрокам :
            for (Player player : game.getPlayers()) {
                User playerUser = player.getUser();
                SendMessage notification = createSendMessage(playerUser.getTelegramId(), createMessageAllPlayers(game,currentPlayerTurn));
                // Отправьте сообщение игроку (используйте ваш механизм отправки сообщений)
                try {
                    longPollingBotConfig.execute(notification);
                } catch (TelegramApiException e) {
                    System.out.println("Error sending message: " + e.getMessage());
                }

            }
            // отправляем сообщение следующему ведущему
            Player nextPlayer=game.getPlayers().get(game.getCurrentPlayerTurn());
            try {
                longPollingBotConfig.execute(createSendMessage(nextPlayer.getUser().getTelegramId(),createNextPlayerMessage()));
            } catch (TelegramApiException e) {
                System.out.println("Error sending message: " + e.getMessage());
            }

            // Отправляем пользователю сообщение об успешной отправке
            return createSendMessage(chatId, "");

        } catch (NumberFormatException e) {
            return createSendMessage(chatId, "Неверный формат кода игры. Код игры должен быть числом.");
        } catch (Exception e) {
            // Обрабатываем исключения
            return createSendMessage(chatId, "Произошла ошибка .");
        }
    }
    private SendMessage createSendMessage(Long chatId, String text) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
    }
    private String createMessageAllPlayers(Game game, Player currentPlayerTurn) {
        StringBuilder sb = new StringBuilder();
        sb.append("вопрос от игрока   ").append(currentPlayerTurn.getNickname()).append(":\n");
        sb.append(game.getLastQuestionAsked().getQuestionText()).append("\n");
        sb.append("Вот ответ от игрока   ").append(game.getLastQuestionAsked().getTargetNickName()).append(":\n");
        sb.append(game.getLastQuestionAsked().getAnswerText()).append(":\n");
        sb.append("Сейчас будет новый вопрос-ответ");
        return sb.toString();
    }
    private String createNextPlayerMessage(){
        StringBuilder sb = new StringBuilder();
        sb.append("Ваш ход! \n");
        sb.append("Введи  /move/код игры/псевдоним игрока/вопрос \n");
        sb.append("например  /move/1234/котик/ты ходишь в музыкальную школу ? ");
        return sb.toString();
    }
}
