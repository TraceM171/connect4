package ipcconnect4.model;

import ipcconnect4.model.Game.Piece;
import ipcconnect4.model.Game.Pos;

/**
 * This class represents a movement or a play in a {@link GameWithAI}
 */
public class MovementAI extends Movement {

    /**
     * Value assigned to this movement, represents its quality. Higher values
     * means more quality
     */
    public int value;

    /**
     * Creates a default MovementAI using {@link Movement#Movement()} and 0 as
     * its value
     */
    public MovementAI() {
        super();
        value = 0;
    }

    /**
     * Creates a MovementAI making a deep copy of the given Pos and Piece, and a
     * value of -1.
     *
     * @param pos
     * @param piece
     */
    public MovementAI(Pos pos, Piece piece) {
        super(pos, piece);
        value = -1;
    }

    /**
     * Creates a MovementAI making a deep copy of the given Pos and value, using
     * {@link Piece.NONE} as its {@link Piece} .
     *
     * @param pos
     * @param value
     */
    public MovementAI(Pos pos, int value) {
        this.pos = new Pos(pos);
        this.value = value;
    }

    /**
     * Creates an empty MovementAI using {@link Movement#Movement()}, where the
     * only useful field is the given value
     *
     * @param value
     */
    public MovementAI(int value) {
        super();
        this.value = value;
    }
}
