package ipcconnect4.model;

import ipcconnect4.model.Game.Piece;
import ipcconnect4.model.Game.Pos;

/**
 * This class represents a movement or a play in a {@link Game}
 */
public class Movement {

    /**
     * Pos of the movement
     */
    public Pos pos;
    /**
     * Piece of the movement
     */
    public Piece piece;

    /**
     * Create a Movement by making a deep copy of all its fields
     *
     * @param movement Movement to copy values from
     */
    public Movement(Movement movement) {
        this(new Pos(movement.pos), movement.piece);
    }

    /**
     * Create a movement with a given Pos and Piece
     *
     * @param pos
     * @param piece
     */
    public Movement(Pos pos, Piece piece) {
        this.pos = pos;
        this.piece = piece;
    }

    /**
     * Create a default movement, with {@link Pos} (-1, -1) and
     * {@link Piece.NONE}
     */
    public Movement() {
        this(new Pos(-1, -1), Piece.NONE);
    }

}
