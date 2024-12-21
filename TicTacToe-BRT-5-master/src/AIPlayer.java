import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstract superclass for AI Player
 */
abstract class AIPlayer {
    protected Seed[][] board;
    protected Seed mySeed;
    protected Seed oppSeed;

    public AIPlayer(Seed[][] board) {
        this.board = board;
    }

    public void setSeed(Seed seed) {
        this.mySeed = seed;
        this.oppSeed = (seed == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
    }

    public abstract int move(); // Abstract method to get the next move
}

/**
 * AI Player using Minimax algorithm with Alpha-Beta Pruning
 */
class AIPlayerMinimax extends AIPlayer {
    private static final int WIN_SCORE = 1000000;
    private static final int SCORE_3_IN_LINE = 100;
    private static final int SCORE_2_IN_LINE = 10;

    public AIPlayerMinimax(Seed[][] board) {
        super(board);
    }

    @Override
    public int move() {
        int[] result = minimax(6, mySeed, Integer.MIN_VALUE, Integer.MAX_VALUE); // Increased depth to 6
        return result[1];
    }

    private int[] minimax(int depth, Seed player, int alpha, int beta) {
        List<Integer> nextMoves = generateMoves();

        // Randomize move order for same-scored positions
        Collections.shuffle(nextMoves);

        int bestScore = (player == mySeed) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int currentScore;
        int bestCol = -1;

        // Return immediately if game is over or maximum depth is reached
        if (nextMoves.isEmpty() || depth == 0) {
            bestScore = evaluate();
            return new int[]{bestScore, bestCol};
        }

        for (int move : nextMoves) {
            int row = getAvailableRow(move);
            board[row][move] = player;

            if (checkWin(row, move, player)) {
                board[row][move] = Seed.NO_SEED;
                return new int[]{player == mySeed ? WIN_SCORE : -WIN_SCORE, move};
            }

            if (player == mySeed) {
                currentScore = minimax(depth - 1, oppSeed, alpha, beta)[0];
                if (currentScore > bestScore) {
                    bestScore = currentScore;
                    bestCol = move;
                }
                alpha = Math.max(alpha, bestScore);
            } else {
                currentScore = minimax(depth - 1, mySeed, alpha, beta)[0];
                if (currentScore < bestScore) {
                    bestScore = currentScore;
                    bestCol = move;
                }
                beta = Math.min(beta, bestScore);
            }

            board[row][move] = Seed.NO_SEED;

            // Alpha-beta pruning
            if (alpha >= beta) {
                break;
            }
        }
        return new int[]{bestScore, bestCol};
    }

    private int evaluate() {
        int score = 0;

        // Evaluate center column control
        for (int row = 0; row < ConnectFour.ROWS; row++) {
            if (board[row][ConnectFour.COLS/2] == mySeed) {
                score += 3;
            } else if (board[row][ConnectFour.COLS/2] == oppSeed) {
                score -= 3;
            }
        }

        // Evaluate horizontal windows
        score += evaluateLines(true);

        // Evaluate vertical windows
        score += evaluateLines(false);

        // Evaluate diagonal windows
        score += evaluateDiagonal();

        return score;
    }

    private int evaluateLines(boolean horizontal) {
        int score = 0;
        int count = 0;
        int empty = 0;

        for (int i = 0; i < (horizontal ? ConnectFour.ROWS : ConnectFour.COLS); i++) {
            for (int j = 0; j <= (horizontal ? ConnectFour.COLS - 4 : ConnectFour.ROWS - 4); j++) {
                count = 0;
                empty = 0;

                for (int k = 0; k < 4; k++) {
                    Seed cell = horizontal ?
                            board[i][j + k] :
                            board[j + k][i];

                    if (cell == mySeed) count++;
                    else if (cell == Seed.NO_SEED) empty++;
                }

                score += evaluateWindow(count, empty);

                // Check for opponent's pieces
                count = 0;
                for (int k = 0; k < 4; k++) {
                    Seed cell = horizontal ?
                            board[i][j + k] :
                            board[j + k][i];

                    if (cell == oppSeed) count++;
                }

                // Block opponent's potential wins
                if (count == 3 && empty == 1) {
                    score -= SCORE_3_IN_LINE * 2;
                }
            }
        }
        return score;
    }

    private int evaluateDiagonal() {
        int score = 0;

        // Check diagonal lines (/)
        for (int row = 3; row < ConnectFour.ROWS; row++) {
            for (int col = 0; col <= ConnectFour.COLS - 4; col++) {
                int myCount = 0;
                int oppCount = 0;
                int empty = 0;

                for (int i = 0; i < 4; i++) {
                    if (board[row - i][col + i] == mySeed) myCount++;
                    else if (board[row - i][col + i] == oppSeed) oppCount++;
                    else empty++;
                }

                score += evaluateWindow(myCount, empty);
                if (oppCount == 3 && empty == 1) score -= SCORE_3_IN_LINE * 2;
            }
        }

        // Check diagonal lines (\)
        for (int row = 0; row <= ConnectFour.ROWS - 4; row++) {
            for (int col = 0; col <= ConnectFour.COLS - 4; col++) {
                int myCount = 0;
                int oppCount = 0;
                int empty = 0;

                for (int i = 0; i < 4; i++) {
                    if (board[row + i][col + i] == mySeed) myCount++;
                    else if (board[row + i][col + i] == oppSeed) oppCount++;
                    else empty++;
                }

                score += evaluateWindow(myCount, empty);
                if (oppCount == 3 && empty == 1) score -= SCORE_3_IN_LINE * 2;
            }
        }

        return score;
    }

    private int evaluateWindow(int count, int empty) {
        int score = 0;
        if (count == 4) score += WIN_SCORE;
        else if (count == 3 && empty == 1) score += SCORE_3_IN_LINE;
        else if (count == 2 && empty == 2) score += SCORE_2_IN_LINE;
        return score;
    }

    private boolean checkWin(int row, int col, Seed seed) {
        // Check horizontal
        int count = 0;
        for (int c = Math.max(0, col - 3); c <= Math.min(ConnectFour.COLS - 1, col + 3); c++) {
            if (board[row][c] == seed) {
                count++;
                if (count == 4) return true;
            } else {
                count = 0;
            }
        }

        // Check vertical
        count = 0;
        for (int r = Math.max(0, row - 3); r <= Math.min(ConnectFour.ROWS - 1, row + 3); r++) {
            if (board[r][col] == seed) {
                count++;
                if (count == 4) return true;
            } else {
                count = 0;
            }
        }

        // Check diagonal (/)
        count = 0;
        int r = row + 3;
        int c = col - 3;
        while (r >= Math.max(0, row - 3) && c <= Math.min(ConnectFour.COLS - 1, col + 3)) {
            if (r < ConnectFour.ROWS && c >= 0 && board[r][c] == seed) {
                count++;
                if (count == 4) return true;
            } else {
                count = 0;
            }
            r--;
            c++;
        }

        // Check diagonal (\)
        count = 0;
        r = row - 3;
        c = col - 3;
        while (r <= Math.min(ConnectFour.ROWS - 1, row + 3) && c <= Math.min(ConnectFour.COLS - 1, col + 3)) {
            if (r >= 0 && c >= 0 && board[r][c] == seed) {
                count++;
                if (count == 4) return true;
            } else {
                count = 0;
            }
            r++;
            c++;
        }

        return false;
    }

    private List<Integer> generateMoves() {
        List<Integer> validMoves = new ArrayList<>();
        // Prioritize center columns
        int[] colOrder = {3, 2, 4, 1, 5, 0, 6};

        for (int col : colOrder) {
            if (board[0][col] == Seed.NO_SEED) {
                validMoves.add(col);
            }
        }
        return validMoves;
    }

    private int getAvailableRow(int col) {
        for (int row = ConnectFour.ROWS - 1; row >= 0; row--) {
            if (board[row][col] == Seed.NO_SEED) {
                return row;
            }
        }
        return -1;
    }
}