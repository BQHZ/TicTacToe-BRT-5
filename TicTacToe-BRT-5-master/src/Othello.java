import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Othello (Reversi): Two-player Graphic version.
 */
public class Othello extends JPanel {
    private static final long serialVersionUID = 1L; // to prevent serializable warning

    // Define named constants for the board
    public static final int ROWS = 8; // Rows
    public static final int COLS = 8; // Columns

    // Cell dimensions
    public static final int CELL_SIZE = 80;
    public static final int CANVAS_WIDTH = CELL_SIZE * COLS;
    public static final int CANVAS_HEIGHT = CELL_SIZE * ROWS;

    // Colors
    public static final Color COLOR_EMPTY = Color.GREEN;
    public static final Color COLOR_PLAYER1 = Color.BLACK;
    public static final Color COLOR_PLAYER2 = Color.WHITE;

    // Game objects
    private int[][] board; // 0 = empty, 1 = player1, 2 = player2
    private int currentPlayer; // 1 or 2
    private JLabel statusBar; // For displaying status messages

    /**
     * Constructor to set up the game UI and components
     */
    public Othello() {
        this.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT + 30));
        this.setBackground(Color.LIGHT_GRAY);

        board = new int[ROWS][COLS]; // Initialize the board
        currentPlayer = 1; // Player 1 starts

        // Initialize the center pieces
        board[3][3] = 2;
        board[3][4] = 1;
        board[4][3] = 1;
        board[4][4] = 2;

        statusBar = new JLabel("Player 1's Turn");
        statusBar.setPreferredSize(new Dimension(CANVAS_WIDTH, 30));
        this.setLayout(new BorderLayout());
        this.add(statusBar, BorderLayout.SOUTH);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = e.getY() / CELL_SIZE;
                int col = e.getX() / CELL_SIZE;
                if (makeMove(row, col)) {
                    repaint();
                    checkGameState();
                }
            }
        });
    }

    /**
     * Make a move and flip opponent's pieces
     */
    private boolean makeMove(int row, int col) {
        if (board[row][col] != 0 || !canMove(row, col, currentPlayer)) {
            return false; // Invalid move
        }

        board[row][col] = currentPlayer;
        flipPieces(row, col, currentPlayer);
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
        statusBar.setText("Player " + currentPlayer + "'s Turn");
        return true;
    }

    /**
     * Check if the player can place a piece at (row, col)
     */
    private boolean canMove(int row, int col, int player) {
        return hasValidDirection(row, col, player);
    }

    /**
     * Check for valid flipping directions
     */
    private boolean hasValidDirection(int row, int col, int player) {
        int opponent = (player == 1) ? 2 : 1;
        for (int dRow = -1; dRow <= 1; dRow++) {
            for (int dCol = -1; dCol <= 1; dCol++) {
                if (dRow == 0 && dCol == 0) continue;
                if (canFlip(row, col, dRow, dCol, player, opponent)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if pieces can be flipped in a specific direction
     */
    private boolean canFlip(int row, int col, int dRow, int dCol, int player, int opponent) {
        int r = row + dRow;
        int c = col + dCol;
        boolean hasOpponent = false;

        while (r >= 0 && r < ROWS && c >= 0 && c < COLS) {
            if (board[r][c] == opponent) {
                hasOpponent = true;
            } else if (board[r][c] == player) {
                return hasOpponent;
            } else {
                break;
            }
            r += dRow;
            c += dCol;
        }
        return false;
    }

    /**
     * Flip opponent's pieces
     */
    private void flipPieces(int row, int col, int player) {
        int opponent = (player == 1) ? 2 : 1;
        for (int dRow = -1; dRow <= 1; dRow++) {
            for (int dCol = -1; dCol <= 1; dCol++) {
                if (dRow == 0 && dCol == 0) continue;
                if (canFlip(row, col, dRow, dCol, player, opponent)) {
                    flipDirection(row, col, dRow, dCol, player);
                }
            }
        }
    }

    /**
     * Flip pieces in a specific direction
     */
    private void flipDirection(int row, int col, int dRow, int dCol, int player) {
        int r = row + dRow;
        int c = col + dCol;

        while (board[r][c] != player) {
            board[r][c] = player;
            r += dRow;
            c += dCol;
        }
    }

    /**
     * Check the game state
     */
    private void checkGameState() {
        if (isBoardFull() || !hasValidMoves()) {
            int player1Score = countPieces(1);
            int player2Score = countPieces(2);
            String winner = player1Score > player2Score ? "Player 1 Wins!" : "Player 2 Wins!";
            if (player1Score == player2Score) winner = "It's a Draw!";
            statusBar.setText(winner + " Click to restart.");
            resetGame();
        }
    }

    /**
     * Check if the board is full
     */
    private boolean isBoardFull() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (board[row][col] == 0) return false;
            }
        }
        return true;
    }

    /**
     * Check if there are valid moves for either player
     */
    private boolean hasValidMoves() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (board[row][col] == 0 && (canMove(row, col, 1) || canMove(row, col, 2))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Count the pieces for a player
     */
    private int countPieces(int player) {
        int count = 0;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (board[row][col] == player) count++;
            }
        }
        return count;
    }

    /**
     * Reset the game
     */
    private void resetGame() {
        board = new int[ROWS][COLS];
        board[3][3] = 2;
        board[3][4] = 1;
        board[4][3] = 1;
        board[4][4] = 2;
        currentPlayer = 1;
        repaint();
    }

    /**
     * Paint the game board
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw grid and pieces
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int x = col * CELL_SIZE;
                int y = row * CELL_SIZE;
                g.setColor(COLOR_EMPTY);
                g.fillRect(x, y, CELL_SIZE, CELL_SIZE);

                g.setColor(Color.BLACK);
                g.drawRect(x, y, CELL_SIZE, CELL_SIZE);

                if (board[row][col] == 1) {
                    g.setColor(COLOR_PLAYER1);
                    g.fillOval(x + 10, y + 10, CELL_SIZE - 20, CELL_SIZE - 20);
                } else if (board[row][col] == 2) {
                    g.setColor(COLOR_PLAYER2);
                    g.fillOval(x + 10, y + 10, CELL_SIZE - 20, CELL_SIZE - 20);
                }
            }
        }
    }
}