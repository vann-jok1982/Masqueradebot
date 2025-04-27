package com.example.masqueradebot.dataBot.servis;

import com.example.masqueradebot.dataBot.model.User;
import com.example.masqueradebot.dataBot.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServis {

    private final UserRepository userRepository;

    public UserServis(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User FindBiId(Long telegramId) {
        return userRepository.findById(telegramId).orElse(null);
    }

    public void SaveUser(Long telegramId, String username) {
        User user = new User();
        user.setTelegramId(telegramId);
        user.setUsername(username);
        user.setRegistrationDate(LocalDateTime.now());
        userRepository.save(user);
    }
}