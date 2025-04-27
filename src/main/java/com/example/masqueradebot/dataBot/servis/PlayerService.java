package com.example.masqueradebot.dataBot.servis;

import com.example.masqueradebot.dataBot.model.Game;
import com.example.masqueradebot.dataBot.model.Player;
import com.example.masqueradebot.dataBot.model.User;
import com.example.masqueradebot.dataBot.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player findPlayerByUserAndGame_GameCode(User user, Long gameCode) {
        return playerRepository.findByUserAndGame_GameCode(user, gameCode);
    }

    public Player savePlayer(Player player) {
        return playerRepository.save(player);
    }

    public List<Player> getPlayersByGameCode(Long gameCode) {
        return playerRepository.findByGame_GameCode(gameCode); // Исправлено: используем findByGame_GameCode
    }

    // Дополнительные методы для работы с игроками
}