package ipcconnect4.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

// ***************************************
// * REFERENCES USED TO CREATE THIS CODE *
// ***************************************
// https://github.com/raulgonzalezcz/Connect4-AI-Java/blob/a7a8d9cd2b58d1c8eb4e2b68debf1789becac232/src/State.java#L140
// Used the code that checks the win state to create our checkNIn,
// and optimized it to only check the strictly necessary positions
//
/**
 * This class represents a Connect4 game. It is a grid of {@link Piece}, being
 * the (0, 0) at the top left corner
 */
public class Game {

    /**
     * Constants that define the game grid
     */
    public static final int ROWS = 6, COLUMNS = 7;

    protected final Piece[][] board = new Piece[ROWS][COLUMNS];
    private Movement lastMove = new Movement();
    protected int rounds = 0;
    protected GameListener listener;

    /**
     * Creates a Game initialising all cells to {@link Piece.NONE}
     */
    public Game() {
        for (Piece[] row : board) {
            Arrays.fill(row, Piece.NONE);
        }
    }

    /**
     * Create a Game by making a deep copy of all its fields. New Game's
     * listener will be null
     *
     * @param game Game to copy values from
     */
    public Game(Game game) {
        for (int i = 0; i < game.board.length; i++) {
            System.arraycopy(game.board[i], 0, board[i], 0, game.board[0].length);
        }
        lastMove = new Movement(game.lastMove);
        rounds = game.rounds;
        listener = null;
    }

    /**
     * Set the GameListener
     *
     * @param listener
     */
    public void setListener(GameListener listener) {
        this.listener = listener;
    }

    /**
     * Get the Piece located at a position
     *
     * @param pos
     * @return piece
     */
    public Piece getPiece(Pos pos) {
        return board[pos.row][pos.column];
    }

    /**
     * Check if a {@link Piece} can be added to a column
     *
     * @param column
     * @return boolean
     */
    public boolean canPutPiece(int column) {
        return getFirstEmptyRow(column) != -1;
    }

    /**
     * Same as {@link Game#putPiece(int, ipcconnect4.model.Game.Piece)} but the
     * added {@link Piece} will be chosen based on the last movement
     *
     * @param column
     */
    public void putPiece(int column) {
        putPiece(column, getNextPiece());
    }

    /**
     * If can put a {@link Piece} in the specified column, will add it,
     * otherwise does nothing.
     *
     * @param column
     * @param piece
     */
    public void putPiece(int column, Piece piece) {
        if (canPutPiece(column)) {
            int nrow = getFirstEmptyRow(column);
            Pos nPos = new Pos(nrow, column);
            board[nrow][column] = piece;
            updateLastMovement(nPos, piece);
            rounds++;
            if (listener != null) {
                listener.onChange(getLastMovement());
                WinInfo wi = getWinner();
                if (wi != null) {
                    listener.onWin(wi);
                }
                if (isFull()) {
                    listener.onFull();
                }
            }
        }
    }

    /**
     * Updates the last movement made.
     *
     * @param pos New movement Pos
     * @param piece New movement Piece
     */
    protected void updateLastMovement(Pos pos, Piece piece) {
        lastMove = new Movement(pos, piece);
    }

    /**
     * Uses last movement to determine which Piece should be added next
     *
     * @return piece
     */
    public Piece getNextPiece() {
        switch (getLastMovement().piece) {
            case NONE:
            case P2:
                return Piece.P1;
            case P1:
                return Piece.P2;
            default:
                return null;
        }
    }

    /**
     * Get last Movement made in this Game
     *
     * @return movement
     */
    public Movement getLastMovement() {
        return lastMove;
    }

    /**
     * Check if this Game is finished. A Game is finished only if there is a
     * winner or the grid is full
     *
     * @return boolean
     */
    public boolean isOver() {
        return getWinner() != null;
    }

    /**
     * Check if the grid is full
     *
     * @return boolean
     */
    protected boolean isFull() {
        for (Piece piece : board[0]) {
            if (piece == Piece.NONE) {
                return false;
            }
        }
        return true;
    }

    /**
     * Divide the board into two halves horizontally, if the number of rows is
     * odd, the lower half will have one more row than the upper one. The bottom
     * half is emptied, and the chips from the top fall to the bottom, leaving
     * the top half empty
     */
    public void emptyBottomHalf() {
        for (int row = ROWS / 2 - 1; row >= 0; row--) {
            board[ROWS / 2 + row] = board[row];
        }
        for (int row = ROWS / 2 - 1; row >= 0; row--) {
            board[row] = new Piece[COLUMNS];
            Arrays.fill(board[row], Piece.NONE);
        }
    }

    /**
     * Get the WinerInfo of this Game, or null if there is none. This method has
     * a quadratic cost, based on the grid size.
     *
     * @return winInfo
     */
    public WinInfo getWinner() {
        if (rounds < 7) {
            return null;
        }

        final WinType[] winType = {null};
        final Pos[] pos = {null};

        checkNIn(4, true, (posW, typeW) -> {
            pos[0] = posW;
            winType[0] = typeW;
        });

        if (winType[0] == null) {
            return null;
        } else {
            int pieces = -1;
            switch (getPiece(pos[0])) {
                case P1:
                    pieces = (rounds + 1) / 2;
                    break;
                case P2:
                    pieces = rounds / 2;
                    break;
            }
            List<Pos> poses = getWinPositions(pos[0], winType[0]);
            return new WinInfo(getPiece(pos[0]), poses, winType[0], pieces);
        }
    }

    /**
     * Using an starting position and a pattern, returns a list of positions
     * that would make such pattern from the given position
     *
     * @param wp Pos, starting position
     * @param winType WinType, pattern
     * @return Positions that form the given pattern
     */
    private List<Pos> getWinPositions(Pos wp, WinType winType) {
        List<Pos> positions = new ArrayList<>(4);

        switch (winType) {
            case ROW:
                positions.add(new Pos(wp.row, wp.column + 0));
                positions.add(new Pos(wp.row, wp.column + 1));
                positions.add(new Pos(wp.row, wp.column + 2));
                positions.add(new Pos(wp.row, wp.column + 3));
                break;
            case COLUMN:
                positions.add(new Pos(wp.row - 0, wp.column));
                positions.add(new Pos(wp.row - 1, wp.column));
                positions.add(new Pos(wp.row - 2, wp.column));
                positions.add(new Pos(wp.row - 3, wp.column));
                break;
            case DIAGONAL_ASC:
                positions.add(new Pos(wp.row - 0, wp.column + 0));
                positions.add(new Pos(wp.row - 1, wp.column + 1));
                positions.add(new Pos(wp.row - 2, wp.column + 2));
                positions.add(new Pos(wp.row - 3, wp.column + 3));
                break;
            case DIAGONAL_DESC:
                positions.add(new Pos(wp.row + 0, wp.column + 0));
                positions.add(new Pos(wp.row + 1, wp.column + 1));
                positions.add(new Pos(wp.row + 2, wp.column + 2));
                positions.add(new Pos(wp.row + 3, wp.column + 3));
                break;
        }

        return positions;
    }

    /**
     * Check if there are n Pieces consecutive, either on a row, column,
     * ascendent diagonal or descendent diagonal. This method has a quadratic
     * cost, based on the grid size
     *
     * @param n Number of consecutive pieces
     * @param brake If true, will stop the search on the first pattern found,
     * otherwise will search for all
     * @param action
     * {@link FoundAction#onFound(ipcconnect4.model.Game.Pos, ipcconnect4.model.Game.WinType)}
     * will be called every time a pattern is found
     */
    protected void checkNIn(int n, boolean brake, FoundAction action) {
        boolean brakeUsed = false;
        //Check by row
        for (int i = ROWS - 1; i >= 0; i--) {
            for (int j = 0; j <= COLUMNS - n && !(brakeUsed && brake); j++) {
                boolean valid = board[i][j] != Piece.NONE;
                for (int k = 1; k < n && valid; k++) {
                    valid = board[i][j] == board[i][j + k];
                }
                if (valid) {
                    brakeUsed = true;
                    action.onFound(new Pos(i, j), WinType.ROW);
                }
            }
        }

        //Check by column
        for (int i = ROWS - 1; i >= n - 1; i--) {
            for (int j = 0; j < COLUMNS && !(brakeUsed && brake); j++) {
                boolean valid = board[i][j] != Piece.NONE;
                for (int k = 1; k < n && valid; k++) {
                    valid = board[i][j] == board[i - k][j];
                }
                if (valid) {
                    brakeUsed = true;
                    action.onFound(new Pos(i, j), WinType.COLUMN);
                }
            }
        }

        //Check by ascendent diagonal
        for (int i = ROWS - 1; i >= n - 1; i--) {
            for (int j = 0; j <= COLUMNS - n && !(brakeUsed && brake); j++) {
                boolean valid = board[i][j] != Piece.NONE;
                for (int k = 1; k < n && valid; k++) {
                    valid = board[i][j] == board[i - k][j + k];
                }
                if (valid) {
                    brakeUsed = true;
                    action.onFound(new Pos(i, j), WinType.DIAGONAL_ASC);
                }
            }
        }

        //Check by descendent diagonal
        for (int i = ROWS - n; i >= 0; i--) {
            for (int j = 0; j <= COLUMNS - n && !(brakeUsed && brake); j++) {
                boolean valid = board[i][j] != Piece.NONE;
                for (int k = 1; k < n && valid; k++) {
                    valid = board[i][j] == board[i + k][j + k];
                }
                if (valid) {
                    brakeUsed = true;
                    action.onFound(new Pos(i, j), WinType.DIAGONAL_DESC);
                }
            }
        }
    }

    /**
     * Check if a Pos is valid in this Game, will be valid only if it is not
     * outside the grid
     *
     * @param pos
     * @return boolean
     */
    protected boolean isValidPos(Pos pos) {
        return !((pos.row <= -1) || (pos.column <= -1) || (pos.row >= ROWS) || (pos.column >= COLUMNS));
    }

    /**
     * Get the first empty row in a given column
     *
     * @param column
     * @return int
     */
    public int getFirstEmptyRow(int column) {
        if (column >= COLUMNS) {
            return -1;
        }
        for (int i = ROWS - 1; i >= 0; i--) {
            if (board[i][column] == Piece.NONE) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Represents a position in the grid
     */
    public static class Pos {

        public int row, column;

        /**
         * Creates a Pos with a given row and column
         *
         * @param row
         * @param column
         */
        public Pos(int row, int column) {
            this.row = row;
            this.column = column;
        }

        /**
         * Create a Pos by making a deep copy of all its fields
         *
         * @param pos Pos to copy values from
         */
        public Pos(Pos pos) {
            this(pos.row, pos.column);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Pos other = (Pos) obj;
            if (this.row != other.row) {
                return false;
            }
            return this.column == other.column;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 89 * hash + this.row;
            hash = 89 * hash + this.column;
            return hash;
        }

        @Override
        public String toString() {
            return "(" + row + ", " + column + ")";
        }

    }

    /**
     * Holds information about a Game winner, win method, positions involved,
     * etc.
     */
    public static class WinInfo {

        /**
         * Piece used to know the winner player
         */
        public final Piece origin;
        /**
         * List of positions that forms the winning pattern
         */
        public final List<Pos> poses;
        /**
         * Pattern used to win
         */
        public final WinType winType;
        /**
         * Number of pieces (rounds) that the winning player has used
         */
        public final int pieces;

        public WinInfo(Piece origin, List<Pos> pos, WinType winType, int pieces) {
            this.origin = origin;
            this.poses = pos;
            this.winType = winType;
            this.pieces = pieces;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final WinInfo other = (WinInfo) obj;
            if (this.pieces != other.pieces) {
                return false;
            }
            if (this.origin != other.origin) {
                return false;
            }
            if (!Objects.equals(this.poses, other.poses)) {
                return false;
            }
            return this.winType == other.winType;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 97 * hash + Objects.hashCode(this.origin);
            hash = 97 * hash + Objects.hashCode(this.poses);
            hash = 97 * hash + Objects.hashCode(this.winType);
            hash = 97 * hash + this.pieces;
            return hash;
        }

    }

    /**
     * Listener used to perform custom actions when using
     * {@link #checkNIn(int, boolean, ipcconnect4.model.Game.FoundAction)}
     */
    protected interface FoundAction {

        /**
         * Called when a pattern is found in checkNIn
         *
         * @param pos Position of the first piece of the pattern found. Starting
         * from left(column 0) to right(column COLUMNS - 1) and then from
         * bottom(row ROWS) to top(row 0)
         * @param type Pattern found
         */
        void onFound(Pos pos, WinType type);
    }

    /**
     * Interface used to listen to events on a Game
     */
    public interface GameListener {

        /**
         * Called when a {@link Piece} has changed
         *
         * @param movement Movement that caused the change
         */
        void onChange(Movement movement);

        /**
         * Called when the last movement made caused a player to win the game
         *
         * @param winInfo WinInfo describing the win type
         */
        void onWin(WinInfo winInfo);

        /**
         * Called when the board is full
         */
        void onFull();
    }

    /**
     * Represents a game piece
     */
    public enum Piece {

        /**
         * Piece owned by player 1
         */
        P1,
        /**
         * Piece owned by player 2
         */
        P2,
        /**
         * Used to represent gaps, owned by nobody
         */
        NONE
    }

    /**
     * Represents the winning method
     */
    public enum WinType {

        /**
         * 4 Pieces in a row
         */
        ROW,
        /**
         * 4 Pieces in a column
         */
        COLUMN,
        /**
         * 4 Pieces in an ascending diagonal
         */
        DIAGONAL_ASC,
        /**
         * 4 Pieces in an descending diagonal
         */
        DIAGONAL_DESC
    }

}
