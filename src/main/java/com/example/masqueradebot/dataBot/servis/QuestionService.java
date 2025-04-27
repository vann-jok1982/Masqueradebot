package com.example.masqueradebot.dataBot.servis;

import com.example.masqueradebot.dataBot.model.Question;
import com.example.masqueradebot.dataBot.repository.QuestionRepository;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Question saveQuestion(Question question) {
        return questionRepository.save(question);
    }

    public Question getQuestionById(Long id) {
        return questionRepository.findById(id).orElse(null);
    }

    // Дополнительные методы для работы с вопросами
}