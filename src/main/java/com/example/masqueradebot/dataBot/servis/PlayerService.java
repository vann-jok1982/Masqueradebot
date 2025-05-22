package com.example.masqueradebot.dataBot.servis;

import com.example.masqueradebot.dataBot.model.Game;
import com.example.masqueradebot.dataBot.model.Player;
import com.example.masqueradebot.dataBot.model.User;
import com.example.masqueradebot.dataBot.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final GameService gameService;

    public PlayerService(PlayerRepository playerRepository, GameService gameService) {
        this.playerRepository = playerRepository;
        this.gameService = gameService;
    }

    public Player findPlayerByUserAndGame_GameCode(User user, Long gameCode) {
        return playerRepository.findByUserAndGame_GameCode(user, gameCode);
    }
    @Transactional
    public Player savePlayer(Player player) {
        return playerRepository.save(player);
    }
    @Transactional
    public void deletePlayer(Player player) {
        playerRepository.delete(player);
    }
//@Transactional
//public void deletePlayer(Player player) {
//    try {
//        // 1. Получаем игру, в которой состоит игрок
//        Game game = player.getGame();
//
//        // 2. Удаляем игрока из списка игроков в игре
//        List<Player> players = game.getPlayers();
//        players.remove(player); // Удаляем игрока из списка
//
//        // 3. Сохраняем изменения в игре
//        gameService.saveGame(game);
//
//        // 4. Удаляем игрока из базы данных
//        playerRepository.delete(player);
//
//        System.out.println("Игрок {} удален из игры {} и из базы данных."+ player.getNickname()+ game.getGameCode());
//
//    } catch (Exception e) {
//        System.out.println("Ошибка при удалении игрока: "+ e);
//        throw new RuntimeException("Ошибка при удалении игрока.", e);
//    }
//}


    public List<Player> getPlayersByGameCode(Long gameCode) {
        return playerRepository.findByGame_GameCode(gameCode); // Исправлено: используем findByGame_GameCode
    }

    // Дополнительные методы для работы с игроками
    public Player findByPlayerNickname(String nickname) {
        return playerRepository.findByNickname(nickname);
    }
    public Player findByPlayer(User user) {
        return playerRepository.findByUser(user);
    }
}