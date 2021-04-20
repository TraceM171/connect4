package ipcconnect4.model;

import ipcconnect4.util.MinMax;
import java.util.LinkedList;

/**
 * This class represents a Connect4 game with an AI as the player 2. The AI will
 * be an instance of {@link MinMax} algorithm
 *
 * @see Game
 */
public class GameWithAI extends Game {

    private final MinMax AI;
    /**
     * Game difficulty
     */
    public final Difficulty difficulty;
    private MovementAI lastMove = new MovementAI();

    /**
     * Same as
     * {@link #GameWithAI(ipcconnect4.model.GameWithAI.Difficulty, java.lang.Long)},
     * using a random seed
     *
     * @param difficulty
     */
    public GameWithAI(Difficulty difficulty) {
        this(difficulty, null);
    }

    /**
     * Create a GameWithAI using {@link Game#Game()} with the given difficulty,
     * specifying the seed for the random choices
     *
     * @param difficulty
     * @param seed
     */
    public GameWithAI(Difficulty difficulty, Long seed) {
        super();
        this.difficulty = difficulty;
        this.AI = new MinMax(difficulty.value, seed);
    }

    /**
     * Create a GameWithAI by making a deep copy of all its fields. New
     * GameWithAI's AI will be null, so this is only used to simulate game
     * states
     *
     * @param game GameWithAI to copy values from
     */
    public GameWithAI(GameWithAI game) {
        super(game);
        difficulty = game.difficulty;
        AI = null;
        lastMove = game.lastMove;
    }

    /**
     * Make a move using the AI as the player
     *
     * @see #getNextAIMovement()
     */
    public void performAIMovement() {
        putPiece(getNextAIMovement().pos.column);
    }

    /**
     * Get the MovementAI that the AI would do next in this GameWithAI status
     *
     * @return movementAI
     */
    public MovementAI getNextAIMovement() {
        return AI.getNextMove(this);
    }

    @Override
    protected void updateLastMovement(Pos pos, Piece piece) {
        lastMove = new MovementAI(pos, piece);
    }

    @Override
    public MovementAI getLastMovement() {
        return lastMove;
    }

    /**
     * Calculate the quality value of this GameWithAI state, a higher value
     * means more possibilities for the AI to win
     *
     * @return int
     */
    public int utilityFunction() {
        //MAX plays 'O'
        // +90 if 'O' wins, -90 'X' wins,
        // +10 if three 'O' in a row, -5 three 'X' in a row,
        // +4 if two 'O' in a row, -1 two 'X' in a row
        int Xlines = 0;
        int Olines = 0;

        if (getWinner() != null) {
            switch (getWinner().origin) {
                case P1:
                    Xlines = Xlines + 90;
                    break;
                case P2:
                    Olines = Olines + 90;
                    break;
            }
        }
        Xlines = Xlines + countNIn(3, Piece.P1) * 10 + countNIn(2, Piece.P1) * 4;
        Olines = Olines + countNIn(3, Piece.P2) * 5 + countNIn(2, Piece.P2);
        return Olines - Xlines;
    }

    /**
     * Get a List of GameWithAI, representing the different states that can be
     * reached from the current state
     *
     * @param piece
     * @return list
     */
    public LinkedList<GameWithAI> getChildren(Piece piece) {
        LinkedList<GameWithAI> children = new LinkedList<>();
        for (int col = 0; col < COLUMNS; col++) {
            if (canPutPiece(col)) {
                GameWithAI child = new GameWithAI(this);
                child.putPiece(col);
                children.add(child);
            }
        }
        return children;
    }

    /**
     * Get how many times the pattern of n consecutive Pieces is found
     *
     * @param n consecutive Pieces
     * @param piece Piece of which to look for a pattern
     * @return int
     */
    public int countNIn(int n, Piece piece) {
        int[] times = {0};
        checkNIn(n, false, (posW, typeW) -> {
            if (getPiece(posW) == piece) {
                times[0]++;
            }
        });
        return times[0];
    }

    /**
     * Represents the level of difficulty that the AI has, higher difficulties
     * means more time to compute the next movement
     */
    public enum Difficulty {
        EASY(1),
        NORMAL(2),
        HARD(5);

        /**
         * Difficulty value, movements that the AI will go ahead, MinMax
         * iterations
         */
        public final int value;

        Difficulty(final int newValue) {
            value = newValue;
        }
    }
}
