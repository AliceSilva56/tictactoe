package com.tictactoe.game.services;

import com.tictactoe.game.entities.User;
import com.tictactoe.game.repositories.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User(username, passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public void updateUserStats(User user, boolean won) {
        if (won) {
            user.setVictories(user.getVictories() + 1);
            user.setScore(user.getScore() + 3);
        } else {
            user.setDefeats(user.getDefeats() + 1);
        }
        userRepository.save(user);
    }

    public void recordDraw(User user) {
        user.setDraws(user.getDraws() + 1);
        user.setScore(user.getScore() + 1);
        userRepository.save(user);
    }
}
