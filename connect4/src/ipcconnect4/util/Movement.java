package ipcconnect4.util;

import ipcconnect4.model.Game.Piece;
import ipcconnect4.model.Game.Pos;

public class Movement {

    public Pos pos;
    public Piece piece;
    public int value;

    public Movement(Pos pos, Piece piece, int value) {
        this.pos = pos;
        this.piece = piece;
        this.value = value;
    }

    public Movement() {
        this(new Pos(-1, -1), null, 0);
    }

    public Movement(Pos pos) {
        this(pos, null, -1);
    }

    public Movement(int value) {
        this(new Pos(-1, -1), null, value);
    }

    public Movement(Pos pos, int value) {
        this(pos, null, value);
    }
    
    public Movement(Pos pos, Piece piece) {
        this(pos, piece, 0);
    }
}
