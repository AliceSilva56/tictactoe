package com.tictactoe.game.controller;

import com.tictactoe.game.entities.User;
import com.tictactoe.game.repositories.UserRepository;
import com.tictactoe.game.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/me/stats")
    public ResponseEntity<?> getUserStats(Authentication authentication) {
        try {
            User user = userService.findByUsername(authentication.getName());
            Map<String, Object> stats = new HashMap<>();
            stats.put("victories", user.getVictories());
            stats.put("defeats", user.getDefeats());
            stats.put("draws", user.getDraws());
            stats.put("score", user.getScore());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching stats");
        }
    }

    @GetMapping("/ranking")
    public ResponseEntity<?> getRanking() {
        try {
            List<User> users = userRepository.findAllByOrderByScoreDesc();
            List<Map<String, Object>> ranking = new ArrayList<>();

            for (int i = 0; i < users.size(); i++) {
                User user = users.get(i);
                Map<String, Object> userData = new HashMap<>();
                userData.put("username", user.getUsername());
                userData.put("score", user.getScore());
                userData.put("position", i + 1);
                ranking.add(userData);
            }

            return ResponseEntity.ok(ranking);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching ranking");
        }
    }
}
