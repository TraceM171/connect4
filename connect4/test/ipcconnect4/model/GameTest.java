package ipcconnect4.model;

import ipcconnect4.model.Game.Piece;
import ipcconnect4.model.Game.Pos;
import ipcconnect4.model.Game.WinInfo;
import ipcconnect4.model.Game.WinType;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class GameTest {

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void multiUse() {
        Game game = new Game();
        game.putPiece(1);
        game.putPiece(1);
        game.putPiece(1);
        game.putPiece(2);
    }

    @Test
    public void canPutPiece_Empty() {
        Game game = new Game();
        assertEquals(game.canPutPiece(0), true);
    }

    @Test
    public void getWinner_Column() {
        WinInfo expected = new WinInfo(
                Piece.P1, new Pos(5, 0), WinType.COLUMN, 4
        );

        Game game = new Game();
        game.putPiece(0);
        game.putPiece(1);
        game.putPiece(0);
        game.putPiece(1);
        game.putPiece(0);
        game.putPiece(1);
        game.putPiece(0);

        WinInfo obtained = game.getWinner();
        assertEquals(expected, obtained);
    }

    @Test
    public void getWinner_Row() {
        WinInfo expected = new WinInfo(
                Piece.P1, new Pos(5, 0), WinType.ROW, 4
        );

        Game game = new Game();
        game.putPiece(0);
        game.putPiece(0);
        game.putPiece(1);
        game.putPiece(1);
        game.putPiece(2);
        game.putPiece(2);
        game.putPiece(3);

        WinInfo obtained = game.getWinner();
        assertEquals(expected, obtained);
    }

    @Test
    public void getWinner_DiagAsc() {
        WinInfo expected = new WinInfo(
                Piece.P1, new Pos(5, 0), WinType.DIAGONAL_ASC, 6
        );

        Game game = new Game();
        game.putPiece(0);
        game.putPiece(1);
        game.putPiece(1);
        game.putPiece(2);
        game.putPiece(2);
        game.putPiece(3);
        game.putPiece(2);
        game.putPiece(3);
        game.putPiece(3);
        game.putPiece(0);
        game.putPiece(3);

        WinInfo obtained = game.getWinner();
        assertEquals(expected, obtained);
    }

    @Test
    public void getWinner_DiagDesc() {
        WinInfo expected = new WinInfo(
                Piece.P1, new Pos(2, 0), WinType.DIAGONAL_DESC, 6
        );

        Game game = new Game();
        game.putPiece(3);
        game.putPiece(2);
        game.putPiece(2);
        game.putPiece(1);
        game.putPiece(0);
        game.putPiece(1);
        game.putPiece(1);
        game.putPiece(0);
        game.putPiece(0);
        game.putPiece(1);
        game.putPiece(0);

        WinInfo obtained = game.getWinner();
        assertEquals(expected, obtained);
    }

}
