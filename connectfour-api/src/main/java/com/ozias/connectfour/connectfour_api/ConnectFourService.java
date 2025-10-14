package com.ozias.connectfour.connectfour_api;

import org.springframework.stereotype.Service;

/**
 * Service layer to hold the state of the single Connect Four game 
 * and manage its lifecycle (start, reset, move execution).
 */
@Service // Marks this class as a Spring Singleton Service
public class ConnectFourService {
    private ConnectFourGame game;

    public ConnectFourService() {
        // Initialize the game when the service starts
        this.game = new ConnectFourGame();
    }

    /** Resets the game by creating a new instance. */
    public void resetGame() {
        this.game = new ConnectFourGame();
    }

    /**
     * Executes an Add or Remove move by calling the core game logic.
     * @param column 1-based index (1-7)
     * @param action "A" for add, "R" for remove
     */
    public void executeMove(int column, String action) {
        // Calls the method we added in the previous step
        this.game.executeMove(column, action);
    }

    /**
     * Retrieves the current state and packages it into the DTO.
     * @return GameState DTO
     */
    public GameState getCurrentState() {
        return new GameState(
            game.getBoardState(),
            game.getWinner(),
            game.getNextPlayer()
        );
    }
}