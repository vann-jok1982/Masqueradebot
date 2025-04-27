package com.example.masqueradebot.dataBot.repository;

import com.example.masqueradebot.dataBot.enam.GameStatus;
import com.example.masqueradebot.dataBot.model.Game;
import com.example.masqueradebot.dataBot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    Game findByGameCode(Long gameCode); // Поиск игры по коду

    boolean existsByGameCode(Long gameCode);

    Game findByCreatorAndStatusNot(User creator, GameStatus status);
}