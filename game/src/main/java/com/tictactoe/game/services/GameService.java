package com.tictactoe.game.services;

import com.tictactoe.game.entities.Game;
import com.tictactoe.game.entities.User;
import com.tictactoe.game.dtos.game.GameStateDTO;
import com.tictactoe.game.repositories.GameRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class GameService {

    private static final Logger log = LoggerFactory.getLogger(GameService.class);
    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Game createGame(User playerX, User playerO) {
        Game game = new Game();
        game.setPlayerX(playerX);
        game.setPlayerO(playerO);
        game.setBoardState("         "); // Tabuleiro vazio com 9 espaços
        return gameRepository.save(game);
    }

    public GameStateDTO makeMove(Game game, int position, String player) {
        char[] board = game.getBoardState().toCharArray();

        // Log para depuração
        log.debug("Making move - Game: {}, Position: {}, Player: {}, Board: {}",
                game.getId(), position, player, game.getBoardState());

        if (board[position] != ' ') {
            throw new IllegalArgumentException("Position already occupied");
        }

        board[position] = player.charAt(0);
        String newBoardState = new String(board);
        game.setBoardState(newBoardState);

        String winner = checkWinner(board);

        if (winner == null && isBoardFull(board)) {
            winner = "DRAW";
        }

        game.setWinner(winner);
        gameRepository.save(game);

        // Log do resultado
        log.debug("Move result - Game: {}, Winner: {}, Board: {}",
                game.getId(), winner, newBoardState);

        return new GameStateDTO(
                convertTo3x3Board(board),
                winner == null ? (player.equals("X") ? "O" : "X") : null,
                winner,
                winner != null
        );
    }

    private String[] convertTo3x3Board(char[] board) {
        String[] result = new String[9];
        for (int i = 0; i < 9; i++) {
            result[i] = String.valueOf(board[i]);
        }
        return result;
    }

    private String checkWinner(char[] board) {
        // Verificar linhas
        for (int i = 0; i < 9; i += 3) {
            if (board[i] != ' ' && board[i] == board[i+1] && board[i] == board[i+2]) {
                return String.valueOf(board[i]);
            }
        }

        // Verificar colunas
        for (int i = 0; i < 3; i++) {
            if (board[i] != ' ' && board[i] == board[i+3] && board[i] == board[i+6]) {
                return String.valueOf(board[i]);
            }
        }

        // Verificar diagonais
        if (board[0] != ' ' && board[0] == board[4] && board[0] == board[8]) {
            return String.valueOf(board[0]);
        }
        if (board[2] != ' ' && board[2] == board[4] && board[2] == board[6]) {
            return String.valueOf(board[2]);
        }

        return null;
    }

    private boolean isBoardFull(char[] board) {
        for (char c : board) {
            if (c == ' ') {
                return false;
            }
        }
        return true;
    }
}

