package com.example.masqueradebot.dataBot.repository;

import com.example.masqueradebot.dataBot.model.Player;
import com.example.masqueradebot.dataBot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

//    List<Player> findByGameId(Long gameId); // Поиск игроков в игре
//    Player findByUserAndGameId(User user, Long gameId); //Поиск игрока по юзеру и id игры

    List<Player> findByGame_GameCode(Long gameCode); // Исправлено имя метода
    Player findByUserAndGame_GameCode(User user, Long gameCode);
}