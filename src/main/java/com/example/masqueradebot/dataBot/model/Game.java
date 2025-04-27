package com.example.masqueradebot.dataBot.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Table(name = "games")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Game {

    @Id
    private Long gameCode;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Player> players=new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private com.example.masqueradebot.dataBot.enam.GameStatus status;

    private Integer currentPlayerTurn;
    private Integer roundNumber;
    private Integer maxPlayers;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Question lastQuestionAsked;

    @ManyToOne
    @JoinColumn(name = "creator_telegram_id", referencedColumnName = "telegram_id")
    private User creator;
}