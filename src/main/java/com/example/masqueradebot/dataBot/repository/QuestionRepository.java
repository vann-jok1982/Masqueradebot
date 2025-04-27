package com.example.masqueradebot.dataBot.repository;

import com.example.masqueradebot.dataBot.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    // Дополнительные методы поиска вопросов, если нужно
}