package com.ozias.connectfour.connectfour_api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

// DTO to map JSON input from the frontend for a move
record MoveRequest(int column, String action) {}

@CrossOrigin(origins = "*") // Allows any JavaScript origin to connect (Crucial for testing)
@RestController 
@RequestMapping("/api/connectfour") 
public class ConnectFourApiController {

    // Spring automatically injects the Service class instance
    private final ConnectFourService gameService;

    public ConnectFourApiController(ConnectFourService gameService) {
        this.gameService = gameService;
    }

    // GET /api/connectfour/status -> Used by frontend to get the current board
    @GetMapping("/status")
    public GameState getStatus() {
        return gameService.getCurrentState();
    }

    // POST /api/connectfour/start -> Resets the game
    @PostMapping("/start")
    public GameState startGame() {
        gameService.resetGame();
        return gameService.getCurrentState();
    }

    // POST /api/connectfour/move -> Submits a move
    @PostMapping("/move")
    public GameState makeMove(@RequestBody MoveRequest move) {
        try {
            // executeMove will throw IllegalArgumentException on invalid input
            gameService.executeMove(move.column(), move.action());
        } catch (IllegalArgumentException e) {
            // Translate the Java Exception into a standard HTTP 400 Bad Request response
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return gameService.getCurrentState();
    }
}