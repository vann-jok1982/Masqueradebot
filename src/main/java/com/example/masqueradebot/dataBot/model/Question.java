package com.example.masqueradebot.dataBot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "questions")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    private Long asker;
    private Long target;

    @Column(length = 1000)  // Adjust length as needed
    private String questionText;

    @Column(length = 1000)  // Adjust length as needed
    private String answerText;

    // No need for explicit getters/setters or constructors thanks to Lombok
}