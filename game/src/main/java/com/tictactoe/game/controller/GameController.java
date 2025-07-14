package com.tictactoe.game.controller;

import com.tictactoe.game.dtos.game.GameMoveDTO;
import com.tictactoe.game.dtos.game.GameStateDTO;
import com.tictactoe.game.exceptions.ResourceNotFoundException;
import com.tictactoe.game.entities.Game;
import com.tictactoe.game.entities.User;
import com.tictactoe.game.repositories.GameRepository;
import com.tictactoe.game.services.GameService;
import com.tictactoe.game.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private static final Logger log = LoggerFactory.getLogger(GameController.class);
    private final GameService gameService;
    private final UserService userService;
    private final GameRepository gameRepository;

    public GameController(
            GameService gameService,
            UserService userService,
            GameRepository gameRepository
    ) {
        this.gameService = gameService;
        this.userService = userService;
        this.gameRepository = gameRepository;
    }

    @PostMapping("/start")
    public ResponseEntity<?> startGame(
            Authentication authentication,
            @RequestParam String opponentUsername
    ) {
        try {
            User currentUser = userService.findByUsername(authentication.getName());
            User opponent = userService.findByUsername(opponentUsername);

            if (opponent == null) {
                return ResponseEntity.badRequest().body("Opponent not found");
            }

            Game game = gameService.createGame(currentUser, opponent);

            return ResponseEntity.ok(Map.of(
                    "gameId", game.getId(),
                    "message", "Game started! You are player X"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/{gameId}/move")
    public ResponseEntity<?> makeMove(
            Authentication authentication,
            @PathVariable Long gameId,
            @RequestBody GameMoveDTO moveDTO
    ) {
        try {
            User currentUser = userService.findByUsername(authentication.getName());
            Game game = gameRepository.findByIdWithPlayers(gameId)
                    .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + gameId));

            log.debug("Move request - Game: {}, User: {}, PlayerX: {}, PlayerO: {}",
                    gameId, currentUser.getId(),
                    game.getPlayerX().getId(), game.getPlayerO().getId());

            if (game.getWinner() != null) {
                return ResponseEntity.badRequest().body("Game is already over");
            }

            String currentPlayerSymbol = determineCurrentPlayerSymbol(game);

            Long currentUserId = currentUser.getId();
            if (!currentUserId.equals(game.getPlayerX().getId()) &&
                    !currentUserId.equals(game.getPlayerO().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not a player in this game");
            }

            if (currentPlayerSymbol.equals("X") && !currentUserId.equals(game.getPlayerX().getId())) {
                return ResponseEntity.badRequest().body("It's not your turn");
            }
            if (currentPlayerSymbol.equals("O") && !currentUserId.equals(game.getPlayerO().getId())) {
                return ResponseEntity.badRequest().body("It's not your turn");
            }

            if (moveDTO.position() < 0 || moveDTO.position() > 8) {
                return ResponseEntity.badRequest().body("Invalid position (must be between 0-8)");
            }

            GameStateDTO gameState = gameService.makeMove(game, moveDTO.position(), currentPlayerSymbol);

            if (gameState.gameOver()) {
                updatePlayerStats(game, gameState.winner());
                return ResponseEntity.ok(new GameStateDTO(
                        gameState.board(),
                        gameState.winner(),
                        gameState.winner(),
                        gameState.gameOver()
                ));
            }

            return ResponseEntity.ok(gameState);
        } catch (Exception e) {
            log.error("Move error", e);
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    private String determineCurrentPlayerSymbol(Game game) {
        long movesCount = game.getBoardState().chars().filter(c -> c != ' ').count();
        return movesCount % 2 == 0 ? "X" : "O";
    }

    @GetMapping
    public ResponseEntity<?> listGames(Authentication authentication) {
        try {
            User currentUser = userService.findByUsername(authentication.getName());
            List<Game> games = gameRepository.findByPlayerXOrPlayerO(currentUser);
            return ResponseEntity.ok(games);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error listing games");
        }
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<?> getGameDetails(
            Authentication authentication,
            @PathVariable Long gameId
    ) {
        try {
            User currentUser = userService.findByUsername(authentication.getName());
            Game game = gameRepository.findByIdWithPlayers(gameId)
                    .orElseThrow(() -> new ResourceNotFoundException("Game not found"));

            if (!currentUser.getId().equals(game.getPlayerX().getId()) &&
                    !currentUser.getId().equals(game.getPlayerO().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
            }

            return ResponseEntity.ok(game);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching game");
        }
    }

    private void updatePlayerStats(Game game, String winner) {
        if ("DRAW".equals(winner)) {
            userService.recordDraw(game.getPlayerX());
            userService.recordDraw(game.getPlayerO());
        } else if ("X".equals(winner)) {
            userService.updateUserStats(game.getPlayerX(), true);
            userService.updateUserStats(game.getPlayerO(), false);
        } else if ("O".equals(winner)) {
            userService.updateUserStats(game.getPlayerO(), true);
            userService.updateUserStats(game.getPlayerX(), false);
        }
    }
}