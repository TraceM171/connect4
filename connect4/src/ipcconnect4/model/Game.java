package ipcconnect4.model;

import ipcconnect4.util.Movement;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;

public class Game {

    public static final int ROWS = 6, COLUMNS = 7;

    private final Piece[][] board  = new Piece[ROWS][COLUMNS];
    private Movement lastMove;
    private int rounds = 0;
    private GameListener listener;

    public Game() {
        for (Piece[] row : board) {
            Arrays.fill(row, Piece.NONE);
        }
    }

    public void setListener(GameListener listener) {
        this.listener = listener;
    }

    public Piece getPiece(Pos pos) {
        return board[pos.row][pos.column];
    }

    public boolean canPutPiece(int column) {
        return getFirstEmptyRow(column) != -1;
    }

    public void putPiece(int column) {
        if (canPutPiece(column)) {
            int nrow = getFirstEmptyRow(column);
            Pos nPos = new Pos(nrow, column);
            board[nrow][column] = getNextPiece();
            lastMove = new Movement(nPos, getNextPiece());
            rounds++;
            if (listener != null) {
                listener.onChange(nPos);
            }
        }
    }

    public Piece getNextPiece() {
        switch (lastMove.piece) {
            case NONE:
            case P2:
                return Piece.P1;
            case P1:
                return Piece.P2;
            default:
                return null;
        }
    }

    public Movement getLastMovement() {
        return lastMove;
    }

    public boolean isOver() {
        return getWinner() != null;
    }

    private boolean isFull() {
        for (Piece piece : board[ROWS - 1]) {
            if (piece == Piece.NONE) {
                return false;
            }
        }
        return true;
    }

    public WinInfo getWinner() {
        if (rounds < 7) {
            return null;
        }

        WinType winType = null;
        Pos pos = null;

        //Winner by row
        for (int i = ROWS - 1; i >= 0; i--) {
            for (int j = 0; winType == null && j < 4; j++) {
                if (board[i][j] == board[i][j + 1] && board[i][j] == board[i][j + 2] && board[i][j] == board[i][j + 3] && board[i][j] != Piece.NONE) {
                    pos = new Pos(i, j);
                    winType = WinType.ROW;
                }
            }
        }

        //Winner by column
        for (int i = 0; i < ROWS - 3; i++) {
            for (int j = 0; winType == null && j < 7; j++) {
                if (board[i][j] == board[i + 1][j] && board[i][j] == board[i + 2][j] && board[i][j] == board[i + 3][j] && board[i][j] != Piece.NONE) {
                    pos = new Pos(i, j);
                    winType = WinType.COLUMN;
                }
            }
        }

        //Winner by ascendent diagonal
        for (int i = 0; i < ROWS - 3; i++) {
            for (int j = 0; winType == null && j < 4; j++) {
                if (board[i][j] == board[i + 1][j + 1] && board[i][j] == board[i + 2][j + 2] && board[i][j] == board[i + 3][j + 3] && board[i][j] != Piece.NONE) {
                    pos = new Pos(i, j);
                    winType = WinType.DIAGONAL_ASC;
                }
            }
        }

        //Winner by descendent diagonal
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; winType == null && j < COLUMNS; j++) {
                if (canMove(new Pos(i - 3, j + 3))) {
                    if (board[i][j] == board[i - 1][j + 1] && board[i][j] == board[i - 2][j + 2] && board[i][j] == board[i - 3][j + 3] && board[i][j] != Piece.NONE) {
                        pos = new Pos(i, j);
                        winType = WinType.DIAGONAL_DESC;
                    }
                }
            }
        }

        if (winType == null) {
            // :(
            return null;
        } else {
            int pieces = -1;
            switch (getPiece(pos)) {
                case P1:
                    pieces = (rounds + 1) / 2;
                    break;
                case P2:
                    pieces = rounds / 2;
                    break;
            }
            return new WinInfo(getPiece(pos), pos, winType, pieces);
        }
    }

    public LinkedList<Game> getChildren(Piece piece) {
        LinkedList<Game> children = new LinkedList<>();
        for (int col = 0; col < COLUMNS && canPutPiece(col); col++) {
            Game child = copyOf(this);
            child.putPiece(col);
            children.add(child);
        }
        return children;
    }
    
    public int utilityFunction() {
        //MAX plays 'O'
        // +90 if 'O' wins, -90 'X' wins,
        // +10 if three 'O' in a row, -5 three 'X' in a row,
        // +4 if two 'O' in a row, -1 two 'X' in a row
        int Xlines = 0;
        int Olines = 0;
        
        switch(getWinner().origin) {
            case P1:
                Xlines = Xlines + 90;
                break;
            case P2:
                Olines = Olines + 90;
                break;
        }	
        Xlines  = Xlines + check3In(Piece.P1)*10 + check2In(Piece.P1)*4;
        Olines  = Olines + check3In(Piece.P2)*5 + check2In(Piece.P2);
	return Olines - Xlines;
    }
    
    //Checks if there are 3 pieces of a same player
    public int check3In(Piece piece) {	
        int times = 0;
        //In row
        for (int i = ROWS; i >= 0; i--) {
            for (int j = 0; j < 7; j++) {
                if (canMove(new Pos(i, j + 2))) {
                    if (board[i][j] == board[i][j + 1] && board[i][j] == board[i][j + 2] && board[i][j] == piece) {
                        times++;
                    }
                }
            }
        }

        //In column
        for (int i = 5; i >= 0; i--) {
            for (int j = 0; j < 7; j++) {
                if (canMove(new Pos(i - 2, j))) {
                    if (board[i][j] == board[i - 1][j] && board[i][j] == board[i - 2][j] && board[i][j] == piece) {
                        times++;
                    }
                }
            }
        }

        //In diagonal ascendent
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                if (canMove(new Pos(i + 2, j + 2))) {
                    if (board[i][j] == board[i + 1][j + 1] && board[i][j] == board[i + 2][j + 2] && board[i][j] == piece) {
                        times++;
                    }
                }
            }
        }

        //In diagonal descendent
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                if (canMove(new Pos(i - 2, j + 2))) {
                    if (board[i][j] == board[i - 1][j + 1] && board[i][j] == board[i - 2][j + 2] && board[i][j] == piece) {
                        times++;
                    }
                }
            }
        }
        return times;				
    }

    //Checks if there are 2 pieces of a same player
    public int check2In(Piece piece) {	
        int times = 0;
        //In a row
        for (int i = 5; i >= 0; i--) {
            for (int j = 0; j < 7; j++) {
                if (canMove(new Pos(i, j + 1))) {
                    if (board[i][j] == board[i][j + 1] && board[i][j] == piece) {
                        times++;
                    }
                }
            }
        }

        //In a column
        for (int i = 5; i >= 0; i--) {
            for (int j = 0; j < 7; j++) {
                if (canMove(new Pos(i - 1, j))) {
                    if (board[i][j] == board[i - 1][j] && board[i][j] == piece) {
                        times++;
                    }
                }
            }
        }

        //In a diagonal ascendent
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                if (canMove(new Pos(i + 1, j + 1))) {
                    if (board[i][j] == board[i + 1][j + 1] && board[i][j] == piece) {
                        times++;
                    }
                }
            }
        }

        //In a diagonal descendent
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                if (canMove(new Pos(i - 1, j + 1))) {
                    if (board[i][j] == board[i - 1][j + 1] && board[i][j] == piece) {
                        times++;
                    }
                }
            }
        }
        return times;
    }

    public static Game copyOf(Game game) {
        Game expansion = new Game();
        expansion.lastMove = game.lastMove;
        expansion.rounds = game.rounds;
        for (int i = 0; i < game.board.length; i++) {
            System.arraycopy(game.board[i], 0, expansion.board[i], 0, game.board[i].length);
        }
        return expansion;
    }

    private boolean canMove(Pos pos) {
        return !((pos.row <= -1) || (pos.column <= -1) || (pos.row > ROWS) || (pos.column > COLUMNS));
    }

    private int getFirstEmptyRow(int column) {
        if (column >= COLUMNS) {
            return -1;
        }
        for (int i = 0; i < ROWS; i++) {
            if (board[i][column] == Piece.NONE) {
                return i;
            }
        }
        return -1;
    }

    public interface GameListener {

        void onChange(Pos pos);
    }

    public enum Piece {
        P1,
        P2,
        NONE
    }

    public enum WinType {
        ROW,
        COLUMN,
        DIAGONAL_ASC,
        DIAGONAL_DESC
    }

    public static class Pos {

        public int row, column;

        public Pos(int row, int column) {
            this.row = row;
            this.column = column;
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
    }

    public static class WinInfo {

        public final Piece origin;
        public final Pos pos;
        public final WinType winType;
        public final int pieces;

        public WinInfo(Piece origin, Pos pos, WinType winType, int pieces) {
            this.origin = origin;
            this.pos = pos;
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
            if (!Objects.equals(this.pos, other.pos)) {
                return false;
            }
            return this.winType == other.winType;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 97 * hash + Objects.hashCode(this.origin);
            hash = 97 * hash + Objects.hashCode(this.pos);
            hash = 97 * hash + Objects.hashCode(this.winType);
            hash = 97 * hash + this.pieces;
            return hash;
        }
        
        
    }
}
