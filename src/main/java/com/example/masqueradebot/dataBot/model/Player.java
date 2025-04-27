package com.example.masqueradebot.dataBot.model;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "players")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Уникальный ID для каждой записи Player

    @ManyToOne
    @JoinColumn(name = "telegram_id", referencedColumnName = "telegram_id")
    private User user; // Связь с сущностью User

    private String nickname;
    private String realName; // Optional
    private Boolean isRevealed;
    private Integer points;

    @ManyToOne(fetch = FetchType.LAZY)  // Eager is often fine here since it's ManyToOne, but be aware of performance
    @JoinColumn(name = "game_code", referencedColumnName = "gameCode") // Foreign key column in the players table
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Game game;
}