package com.ozias.connectfour.connectfour_api;

// Lombok annotation to automatically generate getters, toString, equals, and hashCode methods
import lombok.Getter; 

/**
 * DTO (Data Transfer Object) used to structure the game state 
 * sent from the Java backend to the JavaScript frontend (as JSON).
 */
@Getter 
public class GameState {
    private final String[][] board;
    private final int winner;
    private final int nextPlayer;
    private final boolean isGameOver;

    public GameState(String[][] board, int winner, int nextPlayer) {
        this.board = board;
        this.winner = winner;
        this.nextPlayer = nextPlayer;
        this.isGameOver = (winner != -1);
    }
}