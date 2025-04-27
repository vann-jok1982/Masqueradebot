package com.example.masqueradebot.dataBot.servis;

import com.example.masqueradebot.dataBot.enam.GameStatus;
import com.example.masqueradebot.dataBot.model.Game;
import com.example.masqueradebot.dataBot.model.Player;
import com.example.masqueradebot.dataBot.model.User;
import com.example.masqueradebot.dataBot.repository.GameRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Game createNewGame(int maxPlayers,User creator) {
        Game game = new Game();
        game.setGameCode(generateGameCode());
        game.setStatus(GameStatus.WAITING_FOR_PLAYERS);
        game.setCurrentPlayerTurn(0);
        game.setRoundNumber(1);
        game.setMaxPlayers(maxPlayers);
        game.setCreator(creator);
        return gameRepository.save(game);
    }

    public Game findGameByCode(Long gameCode) {
        return gameRepository.findByGameCode(gameCode);
    }

    public Game saveGame(Game game) {
        return gameRepository.save(game);
    }

    private Long generateGameCode() {
        Long gameCode;
        do {
            UUID uuid = UUID.randomUUID();
            long mostSigBits = uuid.getMostSignificantBits();
            long positiveLong = Math.abs(mostSigBits);
            gameCode = positiveLong % 10000;
        } while (gameRepository.existsByGameCode(gameCode));
        return gameCode;
    }

    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }
    public Game findActiveGameByUser(User user) {
        return gameRepository.findByCreatorAndStatusNot(user, GameStatus.FINISHED);
    }
    public void addPlayerToGame(Game game, Player player) {
        List<Player> players = game.getPlayers();
        players.add(player);
        gameRepository.save(game);
    }

    public void deleteGame(Game activeGame) {
        gameRepository.delete(activeGame);
    }
}