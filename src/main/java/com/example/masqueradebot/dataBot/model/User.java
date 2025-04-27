package com.example.masqueradebot.dataBot.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "users")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data // Lombok аннотация для генерации геттеров, сеттеров, toString, equals и hashCode
public class User {

    @Id
    @Column(name = "telegram_id")
    private Long telegramId;

    @Column(name = "username")
    private String username;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

}