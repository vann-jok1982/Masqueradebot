package com.example.masqueradebot.dataBot.repository;

import com.example.masqueradebot.dataBot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}