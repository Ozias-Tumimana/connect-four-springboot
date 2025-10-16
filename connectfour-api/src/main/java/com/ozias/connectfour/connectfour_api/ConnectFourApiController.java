package com.ozias.connectfour.connectfour_api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

record MoveRequest(int column, String action) {}

@CrossOrigin(origins = "*") 
@RestController 
@RequestMapping("/api/connectfour") 
public class ConnectFourApiController {

    private final ConnectFourService gameService;

    public ConnectFourApiController(ConnectFourService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/status")
    public GameState getStatus() {
        return gameService.getCurrentState();
    }

    @PostMapping("/start")
    public GameState startGame() {
        gameService.resetGame();
        return gameService.getCurrentState();
    }

    @PostMapping("/move")
    public GameState makeMove(@RequestBody MoveRequest move) {
        try {
            gameService.executeMove(move.column(), move.action());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return gameService.getCurrentState();
    }
}