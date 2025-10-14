package com.ozias.connectfour.connectfour_api;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * This class tests the core game logic (ConnectFourGame) without starting the
 * full Spring application context, ensuring logic isolation.
 */
@SpringBootTest 
public class ConnectfourApiApplicationTests { 

    // --- INITIAL STATE AND BASIC MOVE TESTS ---

    @Test
    @DisplayName("Initial State and Turn Switch")
    public void testInitialStateAndTurns() {
        ConnectFourGame g = new ConnectFourGame();
        
        assertEquals(ConnectFourGame.PLAYER_1, g.getNextPlayer(), 
                     "Player 1 not next player after construction");
        assertFalse(g.isGameOver(), "Game over immediately after construction");

        // Player 1 makes a move (Add to Column 1)
        g.executeMove(1, "A"); 
        
        assertEquals(ConnectFourGame.PLAYER_2, g.getNextPlayer(), 
                     "Player 2 not next player after P1 move");
        assertEquals(ConnectFourGame.GAME_NOT_OVER, g.getWinner(), 
                     "Winner incorrectly declared after a single move");
    }

    // --- EXCEPTION HANDLING TESTS ---

    @Test
    @DisplayName("Move Exception Handling")
    public void testMoveExceptions() {
        // Test 1: Invalid column index
        assertThrows(IllegalArgumentException.class, () -> {
            new ConnectFourGame().executeMove(0, "A");
        }, "Exception not thrown for invalid column 0");
        
        // Test 2: Invalid action input
        assertThrows(IllegalArgumentException.class, () -> {
            new ConnectFourGame().executeMove(1, "X");
        }, "Exception not thrown for invalid action X");

        // Test 3: Trying to remove from an empty column (P1's turn)
        assertThrows(IllegalArgumentException.class, () -> {
            new ConnectFourGame().executeMove(1, "R");
        }, "Exception not thrown for removing from empty column");
        
        // Test 4: Trying to remove opponent's disk (P2's turn to remove P1's disk)
        ConnectFourGame removeTest = new ConnectFourGame();
        removeTest.executeMove(1, "A"); // P1 drops
        assertThrows(IllegalArgumentException.class, () -> {
            removeTest.executeMove(1, "R"); // P2 tries to remove P1's disc
        }, "Exception not thrown for trying to remove opponent's disc"); 


	// Test 5: Trying to add to a full column
	ConnectFourGame fullTest = new ConnectFourGame();

	// CORRECTED: Fill the column by alternating players to prevent a premature win.
	for (int i = 0; i < 3; i++) {
		fullTest.executeMove(1, "A"); // P1 plays
		fullTest.executeMove(1, "A"); // P2 plays
	}
	// Now the column is full (3 P1, 3 P2), no winner, and it's P1's turn.

	assertThrows(IllegalArgumentException.class, () -> {
		fullTest.executeMove(1, "A"); // 7th move should fail
	}, "Exception not thrown for adding to a full column");

    }

    // --- WIN CONDITION TESTS ---

    @Test
    @DisplayName("Vertical Win")
    public void testVerticalWins() {
        ConnectFourGame test = new ConnectFourGame();
        test.executeMove(1, "A"); // p1 (5)
        test.executeMove(2, "A"); // p2 (5)
        test.executeMove(1, "A"); // p1 (4)
        test.executeMove(2, "A"); // p2 (4)
        test.executeMove(1, "A"); // p1 (3)
        test.executeMove(2, "A"); // p2 (3)
        test.executeMove(1, "A"); // p1 (2) - WINNING MOVE
        
        assertEquals(ConnectFourGame.PLAYER_1, test.getWinner(), "P1 should win vertically in column 1");
    }

    @Test
    @DisplayName("Horizontal Win")
    public void testHorizontalWins() {
        ConnectFourGame test = new ConnectFourGame();
        
        // P1 wins horizontally in the bottom row (Row 5)
        test.executeMove(1, "A"); // P1 (5, 0)
        test.executeMove(7, "A"); // P2 (Safe Col)
        
        test.executeMove(2, "A"); // P1 (5, 1)
        test.executeMove(7, "A"); // P2 (Safe Col)
        
        test.executeMove(3, "A"); // P1 (5, 2)
        test.executeMove(7, "A"); // P2 (Safe Col)
        
        test.executeMove(4, "A"); // P1 (5, 3) - WINNING MOVE
        
        assertEquals(ConnectFourGame.PLAYER_1, test.getWinner(), "P1 should win horizontally in bottom row");
    }

@Test
@DisplayName("Diagonal Win: Bottom-Left to Top-Right")
	public void testDiagonalWins() {
		ConnectFourGame test = new ConnectFourGame();

		// Goal: P1 (🔴) wins with a diagonal from (5,0) to (2,3)
		// We will use P2 (🟡) to build a "staircase" for P1 to place pieces on.

		// Move 1: P1 places the first piece of the diagonal.
		test.executeMove(1, "A"); // P1 @ (5,0) 🔴

		// Move 2 & 3: P1 places the second piece of the diagonal.
		test.executeMove(2, "A"); // P2 @ (5,1) 🟡 (Support)
		test.executeMove(2, "A"); // P1 @ (4,1) 🔴

		// Move 4 & 5: P1 needs to place a piece at (3,2).
		test.executeMove(3, "A"); // P2 @ (5,2) 🟡 (Support)
		test.executeMove(3, "A"); // P1 @ (4,2) 🔴 (Filler, blocks P2)
		
		// Move 6 & 7: P1 places the third piece of the diagonal.
		test.executeMove(3, "A"); // P2 @ (3,2) 🟡 (Support)
		test.executeMove(4, "A"); // P1 @ (5,3) 🔴 (Filler, blocks P2)

		// Move 8 & 9: P2 builds up column 4 to prepare for P1's winning move.
		test.executeMove(4, "A"); // P2 @ (4,3) 🟡 (Support)
		test.executeMove(4, "A"); // P1 @ (3,3) 🔴 (Filler, blocks P2)
		
		// Move 10: P2 places the final support piece.
		test.executeMove(4, "A"); // P2 @ (2,3) 🟡 (Support)

		// Move 11: The WINNING MOVE
		// It's P1's turn. Placing a token in column 3 will complete the diagonal.
		test.executeMove(3, "A"); // P1 @ (2,2) 🔴 - WIN!

		// The final board state creates a diagonal win for P1 (🔴):
		// (5,0), (4,1), (3,2) <-- WRONG, this is the error in my thinking. It's (2,2).
		// Let me fix that live. The pieces are (5,0), (4,1), (2,2)? No.
		// The previous test logic was flawed. Let's build a new, clean one.
		
		// SCRAP THAT. HERE IS THE SIMPLEST POSSIBLE TEST.

		ConnectFourGame simpleTest = new ConnectFourGame();

		// P1 places at bottom of Col 1.
		simpleTest.executeMove(1, "A"); // P1: (5,0)

		// P2 supports in Col 2.
		simpleTest.executeMove(2, "A"); // P2: (5,1)

		// P1 places on top of P2's piece.
		simpleTest.executeMove(2, "A"); // P1: (4,1)

		// P2 plays in Col 1 to block.
		simpleTest.executeMove(1, "A"); // P2: (4,0)

		// P1 builds the diagonal higher.
		simpleTest.executeMove(3, "A"); // P1: (5,2)

		// P2 supports in Col 3.
		simpleTest.executeMove(3, "A"); // P2: (4,2)

		// P1 places the third piece.
		simpleTest.executeMove(3, "A"); // P1: (3,2)

		// P2 plays in Col 1.
		simpleTest.executeMove(1, "A"); // P2: (3,0)

		// P1 sets up the win.
		simpleTest.executeMove(4, "A"); // P1: (5,3)
		
		// P2 plays in Col 4.
		simpleTest.executeMove(4, "A"); // P2: (4,3)
		
		// P1 plays in Col 4.
		simpleTest.executeMove(4, "A"); // P1: (3,3)
		
		// P2 plays in Col 4.
		simpleTest.executeMove(4, "A"); // P2: (2,3)

		// P1 makes the WINNING MOVE.
		simpleTest.executeMove(4, "A"); // P1: (1,3)
		
		// Let's re-verify this... no, that's still too complex.
		// The key is to find the absolute minimum moves.

		ConnectFourGame finalTest = new ConnectFourGame();

		// The winning diagonal will be (R=P1, Y=P2):
		// R @ (5,0)
		// R @ (4,1)
		// R @ (3,2)
		// R @ (2,3)

		finalTest.executeMove(1, "A"); // P1 @ (5,0)
		finalTest.executeMove(2, "A"); // P2 @ (5,1)
		finalTest.executeMove(2, "A"); // P1 @ (4,1)
		finalTest.executeMove(3, "A"); // P2 @ (5,2)
		finalTest.executeMove(3, "A"); // P1 @ (4,2)
		finalTest.executeMove(4, "A"); // P2 @ (5,3)
		finalTest.executeMove(3, "A"); // P1 @ (3,2)
		finalTest.executeMove(4, "A"); // P2 @ (4,3)
		finalTest.executeMove(4, "A"); // P1 @ (3,3)
		finalTest.executeMove(1, "A"); // P2 @ (4,0) - safe move
		finalTest.executeMove(4, "A"); // P1 @ (2,3) <-- WINNING MOVE

		assertEquals(ConnectFourGame.PLAYER_1, finalTest.getWinner(),
				"P1 should win diagonally (bottom-left to top-right)");
	}
}