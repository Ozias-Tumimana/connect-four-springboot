// --- CONSTANTS & STATE --- //
const API_BASE_URL = 'http://localhost:8080/api/connectfour';
const boardElement = document.getElementById('connect-four-board');
const statusMessageElement = document.getElementById('status-message');
const newGameButton = document.getElementById('new-game-button');

const PLAYER_TOKENS = {
    '⚪': '',          // Empty
    '🔴': 'player1', // Player 1
    '🟡': 'player2'  // Player 2
};

// --- API FUNCTIONS --- //

/** Fetches the current game state from the backend */
async function getGameState() {
    try {
        const response = await fetch(`${API_BASE_URL}/status`);
        if (!response.ok) throw new Error('Failed to fetch game state.');
        const gameState = await response.json();
        updateUI(gameState);
    } catch (error) {
        statusMessageElement.textContent = error.message;
    }
}

/** Starts a new game */
async function startNewGame() {
    try {
        const response = await fetch(`${API_BASE_URL}/start`, { method: 'POST' });
        if (!response.ok) throw new Error('Failed to start a new game.');
        const gameState = await response.json();
        updateUI(gameState);
    } catch (error) {
        statusMessageElement.textContent = error.message;
    }
}

/** Makes a move in the specified column */
async function makeMove(column) {
    try {
        const response = await fetch(`${API_BASE_URL}/move`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ column: column + 1, action: 'A' }) // API is 1-based
        });

        if (!response.ok) {
            const errorData = await response.text();
            throw new Error(errorData);
        }

        const gameState = await response.json();
        updateUI(gameState);
    } catch (error) {
        // Display backend error message directly
        statusMessageElement.textContent = error.message;
    }
}

// --- UI RENDERING --- //

/** Updates the entire UI based on the game state from the backend */
function updateUI(gameState) {
    renderBoard(gameState.board);
    updateStatusMessage(gameState);
}

/** Renders the game board */
function renderBoard(board) {
    boardElement.innerHTML = ''; // Clear previous board state
    for (let row = 0; row < 6; row++) {
        for (let col = 0; col < 7; col++) {
            const cell = document.createElement('div');
            cell.classList.add('board-cell');
            cell.dataset.column = col; // Store column index in data attribute

            const token = document.createElement('div');
            token.classList.add('token');
            
            // Get the CSS class for the token ('player1', 'player2', or '')
            const tokenSymbol = board[row][col];
            const tokenClass = PLAYER_TOKENS[tokenSymbol];
            if (tokenClass) {
                token.classList.add(tokenClass);
            }
            
            cell.appendChild(token);
            boardElement.appendChild(cell);
        }
    }
}

/** Updates the status message based on winner and next player */
function updateStatusMessage(gameState) {
    if (gameState.gameOver) {
        if (gameState.winner === 1) {
            statusMessageElement.textContent = 'Player 1 (🔴) Wins!';
        } else if (gameState.winner === 2) {
            statusMessageElement.textContent = 'Player 2 (🟡) Wins!';
        } else {
            statusMessageElement.textContent = 'It\'s a Tie!';
        }
    } else {
        const nextPlayerToken = gameState.nextPlayer === 1 ? '🔴' : '🟡';
        statusMessageElement.textContent = `Player ${gameState.nextPlayer}'s Turn ${nextPlayerToken}`;
    }
}


// --- EVENT LISTENERS --- //
newGameButton.addEventListener('click', startNewGame);

boardElement.addEventListener('click', (event) => {
    // Check if a cell was clicked
    if (event.target.classList.contains('board-cell')) {
        const column = parseInt(event.target.dataset.column, 10);
        makeMove(column);
    } else if (event.target.parentElement.classList.contains('board-cell')) {
        // Check if the token inside a cell was clicked
        const column = parseInt(event.target.parentElement.dataset.column, 10);
        makeMove(column);
    }
});


// --- INITIAL LOAD --- //
// Load the game state when the page first opens
startNewGame();
