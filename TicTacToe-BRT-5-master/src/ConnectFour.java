import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.URL;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Arrays;

/**
 * Connect Four: Two-player Graphic version with GIF tokens.
 */
public class ConnectFour extends JPanel {
    private static final long serialVersionUID = 1L; // to prevent serializable warning

    // Define named constants for the board
    public static final int ROWS = 6; // Rows
    public static final int COLS = 7; // Columns

    // Cell dimensions
    public static final int CELL_SIZE = 100;
    public static final int CANVAS_WIDTH = CELL_SIZE * COLS;
    public static final int CANVAS_HEIGHT = CELL_SIZE * ROWS;

    // Game objects
    private Seed[][] board; // NO_SEED, NOUGHT, CROSS
    private Seed currentPlayer; // NOUGHT or CROSS
    private JLabel statusBar; // For displaying status messages

    // Images for tokens
    private Image noughtImage;
    private Image crossImage;
    private Image drawImage;

    // AI Player
    private AIPlayer aiPlayer;

    /**
     * Constructor to set up the game UI and components
     */
    public ConnectFour() {
        this.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT + 30));
        this.setBackground(Color.LIGHT_GRAY);

        try {
            noughtImage = ImageIO.read(getClass().getClassLoader().getResource("peepo.gif"));
            crossImage = ImageIO.read(getClass().getClassLoader().getResource("yyLdHR.gif"));
            drawImage = ImageIO.read(getClass().getClassLoader().getResource("sadgecry-sadge.gif"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        board = new Seed[ROWS][COLS]; // Initialize the board
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                board[row][col] = Seed.NO_SEED;
            }
        }
        currentPlayer = Seed.NOUGHT; // Player NOUGHT starts

        aiPlayer = new AIPlayerMinimax(board);
        aiPlayer.setSeed(Seed.CROSS);

        statusBar = new JLabel("Player 1's Turn");
        statusBar.setPreferredSize(new Dimension(CANVAS_WIDTH, 30));

        JButton resetButton = new JButton("Reset Game");
        resetButton.addActionListener(e -> resetGame());

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        controlPanel.add(statusBar, BorderLayout.CENTER);
        controlPanel.add(resetButton, BorderLayout.EAST);

        this.setLayout(new BorderLayout());
        this.add(controlPanel, BorderLayout.SOUTH);

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentPlayer == Seed.CROSS) {
                    return; // Skip clicks during AI's turn
                }

                int colSelected = e.getX() / CELL_SIZE;
                if (colSelected >= 0 && colSelected < COLS) {
                    for (int row = ROWS - 1; row >= 0; row--) {
                        if (board[row][colSelected] == Seed.NO_SEED) {
                            board[row][colSelected] = currentPlayer; // Make a move
                            SoundEffect.KLIK.play();
                            repaint();
                            if (hasWon(currentPlayer, row, colSelected)) {
                                statusBar.setText((currentPlayer == Seed.NOUGHT ? "Player 1" : "Player 2") + " Wins! Click Reset to play again.");
                                SoundEffect.MENANG.play();
                                repaint();
                                return;
                            } else if (isDraw()) {
                                statusBar.setText("It's a Draw! Click Reset to play again.");
                                SoundEffect.KALAH.play();
                                repaint();
                                return;
                            }
                            currentPlayer = (currentPlayer == Seed.NOUGHT) ? Seed.CROSS : Seed.NOUGHT;
                            statusBar.setText((currentPlayer == Seed.NOUGHT ? "Player 1" : "Player 2") + "'s Turn");
                            repaint();

                            if (currentPlayer == Seed.CROSS) {
                                aiMove();
                            }
                            break;
                        }
                    }
                }
            }
        });
    }

    /**
     * AI makes its move
     */
    private void aiMove() {
        int col = aiPlayer.move();
        for (int row = ROWS - 1; row >= 0; row--) {
            if (board[row][col] == Seed.NO_SEED) {
                board[row][col] = currentPlayer;
                SoundEffect.KLIK.play();
                repaint();
                if (hasWon(currentPlayer, row, col)) {
                    statusBar.setText("AI Wins! Click Reset to play again.");
                    SoundEffect.MENANG.play();
                    repaint();
                    return;
                } else if (isDraw()) {
                    statusBar.setText("It's a Draw! Click Reset to play again.");
                    SoundEffect.KALAH.play();
                    repaint();
                    return;
                }
                currentPlayer = (currentPlayer == Seed.NOUGHT) ? Seed.CROSS : Seed.NOUGHT;
                statusBar.setText((currentPlayer == Seed.NOUGHT ? "Player 1" : "Player 2") + "'s Turn");
                repaint();
                break;
            }
        }
    }

    /**
     * Check for a winner
     */
    public boolean hasWon(Seed theSeed, int rowSelected, int colSelected) {
        // Check row
        int count = 0;
        for (int col = 0; col < COLS; ++col) {
            if (board[rowSelected][col] == theSeed) {
                ++count;
                if (count == 4) return true;
            } else {
                count = 0;
            }
        }

        // Check column
        count = 0;
        for (int row = 0; row < ROWS; ++row) {
            if (board[row][colSelected] == theSeed) {
                ++count;
                if (count == 4) return true;
            } else {
                count = 0;
            }
        }

        // Check diagonal (\)
        count = 0;
        for (int d = -Math.min(rowSelected, colSelected); d < Math.min(ROWS - rowSelected, COLS - colSelected); d++) {
            if (board[rowSelected + d][colSelected + d] == theSeed) {
                ++count;
                if (count == 4) return true;
            } else {
                count = 0;
            }
        }

        // Check anti-diagonal (/)
        count = 0;
        for (int d = -Math.min(rowSelected, COLS - colSelected - 1); d < Math.min(ROWS - rowSelected, colSelected + 1); d++) {
            if (board[rowSelected + d][colSelected - d] == theSeed) {
                ++count;
                if (count == 4) return true;
            } else {
                count = 0;
            }
        }

        return false; // No winner
    }

    /**
     * Check if the game is a draw
     */
    private boolean isDraw() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (board[row][col] == Seed.NO_SEED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Reset the game
     */
    private void resetGame() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                board[row][col] = Seed.NO_SEED;
            }
        }
        currentPlayer = Seed.NOUGHT;
        statusBar.setText("Player 1's Turn");
        repaint();
    }

    /** Paint the game board */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw grid and tokens
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int x = col * CELL_SIZE;
                int y = row * CELL_SIZE;

                g.setColor(Color.WHITE);
                g.fillRect(x, y, CELL_SIZE, CELL_SIZE);

                g.setColor(Color.BLACK);
                g.drawRect(x, y, CELL_SIZE, CELL_SIZE);

                if (board[row][col] == Seed.NOUGHT) {
                    g.drawImage(noughtImage, x + 10, y + 10, CELL_SIZE - 20, CELL_SIZE - 20, null);
                } else if (board[row][col] == Seed.CROSS) {
                    g.drawImage(crossImage, x + 10, y + 10, CELL_SIZE - 20, CELL_SIZE - 20, null);
                } else if (board[row][col] == Seed.NO_SEED && isDraw()) {
                    g.drawImage(drawImage, x + 10, y + 10, CELL_SIZE - 20, CELL_SIZE - 20, null);
                }
            }
        }
    }
}