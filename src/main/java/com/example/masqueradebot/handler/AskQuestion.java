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
public class AskQuestion implements CommandHandler{
    private final GameService gameService;
    private final PlayerService playerService;
    private final UserServis userServis;
    private final QuestionService questionService;
    private final LongPollingBotConfig longPollingBotConfig;

    public AskQuestion(GameService gameService, PlayerService playerService, UserServis userServis, QuestionService questionService, @Lazy LongPollingBotConfig longPollingBotConfig) {
        this.gameService = gameService;
        this.playerService = playerService;
        this.userServis = userServis;
        this.questionService = questionService;
        this.longPollingBotConfig = longPollingBotConfig;
    }

    @Override
    @Transactional
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
            if (parts.length != 5 || !parts[1].equals("move")) {
                return createSendMessage(chatId, "Неверный формат команды. Используйте: /move/1234/котик/ты ходишь в музыкальную школу ?");
            }

            // Извлекаем код игры и псевдоним
            Long gameCode = Long.parseLong(parts[2]);
            String nicknamePars = parts[3];
            String questionPars = parts[4];

            // Находим игру по коду
            Game game = gameService.findGameByCode(gameCode);

            if (game == null) {
                return createSendMessage(chatId, "Игра с кодом " + gameCode + " не найдена.");
            }
            if (game.getStatus()!=GameStatus.IN_PROGRESS){
                return createSendMessage(chatId, "Вы не можете задавать вопрос сейчас ");
            }
            Player currentPlayerTurn=game.getPlayers().get(game.getCurrentPlayerTurn());
            System.out.println("currentPlayerTurn = " + currentPlayerTurn);
            User userTurn=currentPlayerTurn.getUser();

            User target = playerService.findByPlayerNickname(nicknamePars).getUser();
            System.out.println("User target = "+target);
            if (user!=userTurn ){
                return createSendMessage(chatId, "Вопрос сейчас задаёт другой игрок ");
            }
            // Добавляем вопрос
            Question question=new Question();
            question.setQuestionText(questionPars);
            question.setAsker(chatId);
            question.setTarget(target.getTelegramId());
            question.setTargetNickName(nicknamePars);
            questionService.saveQuestion(question);
            game.setLastQuestionAsked(question);
            game.setStatus(GameStatus.WAITING_FOR_ANSWER);
            gameService.saveGame(game);


            //отправляем вопрос игроку
            try {
                longPollingBotConfig.execute(createSendMessage(target.getTelegramId(),"Игрок "+currentPlayerTurn.getNickname()  +" задал вопрос : \n" +
                        questionPars + "\n чтобы ответить введите : \n /answer/код игры/ваш ответ"));
            } catch (TelegramApiException e) {
                System.out.println("Error sending message: " + e.getMessage());
            }
            // Отправляем пользователю сообщение об успешной отправке
            return createSendMessage(chatId, "Вы успешно отправили свой вопрос");

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
}