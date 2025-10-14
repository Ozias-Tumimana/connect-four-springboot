package com.ozias.connectfour.connectfour_api;

import java.util.Arrays;

/**
 * This class represents the core logic and state for the Connect Four game.
 * It is designed to be used as a service by a Spring Boot API, handling game rules
 * without direct user input (Scanner).
 */
public class ConnectFourGame {
    public static final String PLAYER_1_TOKEN = "🔴";
    public static final String PLAYER_2_TOKEN = "🟡";
    public static final String EMPTY_TOKEN = "⚪";
    public static final int PLAYER_1 = 1;
    public static final int PLAYER_2 = 2;
    public static final int TIE = 0;
    public static final int GAME_NOT_OVER = -1;

    private static final int BOARD_ROWS = 6;
    private static final int BOARD_COLS = 7;
    private static final int CONNECT_N = 4;

    private String[][] board;
    private boolean isPlayer1Turn;

    /**
     * Constructs a new ConnectFour object with a 7 wide by 6 tall game board.
     */
    public ConnectFourGame() {
        // Initialize board
        board = new String[BOARD_ROWS][BOARD_COLS];
        for (String[] row : board) {
            Arrays.fill(row, EMPTY_TOKEN);
        }
        isPlayer1Turn = true;
    }

    // --- Core State Getters for API ---

    /**
     * Returns a deep copy of the board state to ensure the internal game state
     * cannot be modified from outside the class (encapsulation).
     * @return A copy of the current game board.
     */
    public String[][] getBoardState() {
        // --- POLISH 2: Returning a deep copy instead of a direct reference ---
        return Arrays.stream(board).map(String[]::clone).toArray(String[][]::new);
    }

    public int getNextPlayer() {
        if (isGameOver()) {
            return -1;
        }
        return isPlayer1Turn ? PLAYER_1 : PLAYER_2;
    }

    // ... (The rest of the file remains exactly the same) ...

    // Checks all win conditions and tie
    public int getWinner() {
        // 1. Check Vertical Wins
        for (int i = 0; i < BOARD_COLS; i++) {
            int colWinner = getColWinner(i);
            if (colWinner != GAME_NOT_OVER) {
                return colWinner;
            }
        }

        // 2. Check Horizontal Wins
        for (int i = 0; i < BOARD_ROWS; i++) {
            int rowWinner = getRowWinner(i);
            if (rowWinner != GAME_NOT_OVER) {
                return rowWinner;
            }
        }
        
        // 3. Check Diagonal Wins
        int diagWinner = getDiagonalWinner();
        if (diagWinner != GAME_NOT_OVER) {
            return diagWinner;
        }

        // 4. Check Tie
        return checkTie();
    }

    public boolean isGameOver() {
        return getWinner() != GAME_NOT_OVER;
    }

    public void executeMove(int column, String action) {
        if (isGameOver()) {
            throw new IllegalArgumentException("Game is already over.");
        }
        
        int colIndex = column - 1; 

        if (colIndex < 0 || colIndex >= BOARD_COLS) {
            throw new IllegalArgumentException("Invalid column index: " + column);
        }

        String currDisc = isPlayer1Turn ? PLAYER_1_TOKEN : PLAYER_2_TOKEN;

        if (action.equalsIgnoreCase("A")) {
            makeAddMove(colIndex, currDisc);
        } else if (action.equalsIgnoreCase("R")) {
            makeRemoveMove(colIndex, currDisc);
        } else {
            throw new IllegalArgumentException("Invalid action. Must be 'A' (Add) or 'R' (Remove).");
        }

        isPlayer1Turn = !isPlayer1Turn;
    }

    private void makeRemoveMove(int column, String currDisc) {
        if (board[BOARD_ROWS - 1][column].equals(EMPTY_TOKEN)) {
            throw new IllegalArgumentException("No discs have been played in this column to remove.");
        }
        if (!board[BOARD_ROWS - 1][column].equals(currDisc)) {
            throw new IllegalArgumentException("Can only remove discs that belong to you.");
        }

        for (int row = BOARD_ROWS - 1; row >= 0; row--) {
            if (row == 0) {
                board[row][column] = EMPTY_TOKEN;
            } else {
                board[row][column] = board[row - 1][column];
            }
        }
    }

    private void makeAddMove(int column, String currDisc) {
        if (board[0][column].equals(PLAYER_1_TOKEN) || board[0][column].equals(PLAYER_2_TOKEN)) {
            throw new IllegalArgumentException("Chosen column is already full.");
        }
        for (int i = BOARD_ROWS - 1; i >= 0; i--) {
            if (board[i][column].equals(EMPTY_TOKEN)) {
                board[i][column] = currDisc;
                return;
            }
        }
    }
    
    private int getRowWinner(int row) {
        for (int col = 0; col <= BOARD_COLS - CONNECT_N; col++) {
            String currDisc = board[row][col];
            if (currDisc.equals(EMPTY_TOKEN)) continue;

            if (board[row][col + 1].equals(currDisc) &&
                board[row][col + 2].equals(currDisc) &&
                board[row][col + 3].equals(currDisc)) {
                return getPlayer(currDisc);
            }
        }
        return GAME_NOT_OVER;
    }

    private int checkTie() {
        for (int col = 0; col < BOARD_COLS; col++) {
            if (board[0][col].equals(EMPTY_TOKEN)) {
                return GAME_NOT_OVER;
            }
        }
        return TIE;
    }

    private int getColWinner(int col) {
        for (int row = BOARD_ROWS - 1; row >= CONNECT_N - 1; row--) {
            String currDisc = board[row][col];
            if (currDisc.equals(EMPTY_TOKEN)) continue;

            if (board[row - 1][col].equals(currDisc) &&
                board[row - 2][col].equals(currDisc) &&
                board[row - 3][col].equals(currDisc)) {
                return getPlayer(currDisc);
            }
        }
        return GAME_NOT_OVER;
    }
    
    private int getDiagonalWinner() {
        for (int row = 0; row < BOARD_ROWS; row++) {
            for (int col = 0; col < BOARD_COLS; col++) {
                String token = board[row][col];
                if (token.equals(EMPTY_TOKEN)) {
                    continue;
                }
                
                int boundary = CONNECT_N - 1;

                // Check Down-Right
                if (row <= BOARD_ROWS - CONNECT_N && col <= BOARD_COLS - CONNECT_N) {
                    if (board[row + 1][col + 1].equals(token) &&
                        board[row + 2][col + 2].equals(token) &&
                        board[row + 3][col + 3].equals(token)) {
                        return getPlayer(token);
                    }
                }

                // Check Down-Left
                if (row <= BOARD_ROWS - CONNECT_N && col >= boundary) {
                    if (board[row + 1][col - 1].equals(token) &&
                        board[row + 2][col - 2].equals(token) &&
                        board[row + 3][col - 3].equals(token)) {
                        return getPlayer(token);
                    }
                }

                // Check Up-Right
                if (row >= boundary && col <= BOARD_COLS - CONNECT_N) {
                    if (board[row - 1][col + 1].equals(token) &&
                        board[row - 2][col + 2].equals(token) &&
                        board[row - 3][col + 3].equals(token)) {
                        return getPlayer(token);
                    }
                }

                // Check Up-Left
                if (row >= boundary && col >= boundary) {
                    if (board[row - 1][col - 1].equals(token) &&
                        board[row - 2][col - 2].equals(token) &&
                        board[row - 3][col - 3].equals(token)) {
                        return getPlayer(token);
                    }
                }
            }
        }
        return GAME_NOT_OVER;
    }

    private int getPlayer(String disc) {
        if (disc.equals(PLAYER_1_TOKEN)) {
            return PLAYER_1;
        } else if (disc.equals(PLAYER_2_TOKEN)) {
            return PLAYER_2;
        }
        return GAME_NOT_OVER;
    }

    public void toggleTurn() {
        this.isPlayer1Turn = !this.isPlayer1Turn;
    }
}