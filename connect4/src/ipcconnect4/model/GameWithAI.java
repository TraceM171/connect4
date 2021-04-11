package ipcconnect4.model;

import ipcconnect4.util.MinMax;
import java.util.LinkedList;

/**
 * This class represents a Connect4 game with an AI as the player 2.
 * @see Game
 */
public class GameWithAI extends Game {

    private final MinMax AI;
    private final Difficulty difficulty;
    private MovementAI lastMove = new MovementAI();

    public GameWithAI(Difficulty difficulty) {
        this(difficulty, null);
    }

    public GameWithAI(Difficulty difficulty, Long seed) {
        super();
        this.difficulty = difficulty;
        this.AI = new MinMax(difficulty.value, seed);
    }

    public GameWithAI(GameWithAI game) {
        super(game);
        difficulty = game.difficulty;
        AI = new MinMax(difficulty.value, game.AI.seed);
        lastMove = game.lastMove;
    }

    public void performAIMovement() {
        putPiece(getNextAIMovement().pos.column);
    }

    public Movement getNextAIMovement() {
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

    // Methods needed for the AI
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
        Xlines = Xlines + check3In(Piece.P1) * 10 + check2In(Piece.P1) * 4;
        Olines = Olines + check3In(Piece.P2) * 5 + check2In(Piece.P2);
        return Olines - Xlines;
    }

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

    public int check3In(Piece piece) {
        int[] times = {0};
        checkNIn(3, false, (posW, typeW) -> {
            if (getPiece(posW) == piece) {
                times[0]++;
            }
        });
        return times[0];
    }

    public int check2In(Piece piece) {
        int[] times = {0};
        checkNIn(2, false, (posW, typeW) -> {
            if (getPiece(posW) == piece) {
                times[0]++;
            }
        });
        return times[0];
    }

    public enum Difficulty {
        EASY(1),
        NORMAL(2),
        HARD(5);

        private final int value;

        Difficulty(final int newValue) {
            value = newValue;
        }
    }
}
