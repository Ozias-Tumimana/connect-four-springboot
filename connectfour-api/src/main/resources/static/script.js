const API_BASE_URL = 'https://connect-four-api-ozias.onrender.com';
const boardElement = document.getElementById('connect-four-board');
const statusMessageElement = document.getElementById('status-message');
const newGameButton = document.getElementById('new-game-button');
const dropSound = document.getElementById('drop-sound');

const winScreen = document.getElementById('win-screen');
const winMessage = document.getElementById('win-message');
const loserMessage = document.getElementById('loser-message');
const playAgainButton = document.getElementById('play-again-button');


const PLAYER_TOKENS = {
    '⚪': '',
    '🟣': 'player1',
    '🟡': 'player2'
};

// --- API FUNCTIONS --- //

async function startNewGame() {
    winScreen.classList.add('hidden');
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
            body: JSON.stringify({ column: column + 1, action: 'A' })
        });

        if (!response.ok) {
            const errorData = await response.json(); 
            throw new Error(errorData.message);
        }

        const gameState = await response.json();
        updateUI(gameState);
        dropSound.currentTime = 0;
        dropSound.play();

    } catch (error) {
        let friendlyMessage;

        if (error.message.includes("Game is already over")) {
            friendlyMessage = "Game is over! Click 'New Game' to play again.";
        } else if (error.message.includes("Chosen column is already full")) {
            friendlyMessage = "That column is full! Try another.";
        } else if (error.message.includes("Failed to fetch")) {
            friendlyMessage = "Could not connect to the game server.";
        } else {
            friendlyMessage = "Illegal Move, Try Again!";
        }
        statusMessageElement.textContent = friendlyMessage;
    }
}


// --- UI RENDERING --- //

function updateUI(gameState) {
    renderBoard(gameState.board);
    updateStatusMessage(gameState);

    if (gameState.gameOver && gameState.winner !== 0) {
        showWinScreen(gameState.winner);
    }
}

/** Renders the game board */
function renderBoard(board) {
    boardElement.innerHTML = '';
    for (let row = 0; row < 6; row++) {
        for (let col = 0; col < 7; col++) {
            const cell = document.createElement('div');
            cell.classList.add('board-cell');
            cell.dataset.column = col;

            const token = document.createElement('div');
            token.classList.add('token');
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
            statusMessageElement.textContent = 'Player 1 (🟣) Wins!';
        } else if (gameState.winner === 2) {
            statusMessageElement.textContent = 'Player 2 (🟡) Wins!';
        } else {
            statusMessageElement.textContent = 'It\'s a Tie!';
        }
    } else {
        const nextPlayerToken = gameState.nextPlayer === 1 ? '🟣' : '🟡';
        statusMessageElement.textContent = `Player ${gameState.nextPlayer}'s Turn ${nextPlayerToken}`;
    }
}

/** NEW: Function to show and populate the win screen */
function showWinScreen(winner) {
    const winnerToken = winner === 1 ? 'Player 1 (🟣)' : 'Player 2 (🟡)';
    const loserToken = winner === 1 ? 'Player 2 (🟡)' : 'Player 1 (🟣)';

    winMessage.textContent = `Good job, ${winnerToken}!`;
    loserMessage.textContent = `Try again, ${loserToken}!`;
    
    // Make the win screen visible
    winScreen.classList.remove('hidden');
}

// --- EVENT LISTENERS --- //
newGameButton.addEventListener('click', startNewGame);
playAgainButton.addEventListener('click', startNewGame); 

boardElement.addEventListener('click', (event) => {
    if (event.target.classList.contains('board-cell')) {
        const column = parseInt(event.target.dataset.column, 10);
        makeMove(column);
    } else if (event.target.parentElement.classList.contains('board-cell')) {
        const column = parseInt(event.target.parentElement.dataset.column, 10);
        makeMove(column);
    }
});

startNewGame();