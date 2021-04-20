package ipcconnect4.model;

import ipcconnect4.model.Game.Piece;
import ipcconnect4.model.Game.Pos;
import ipcconnect4.model.Game.WinInfo;
import ipcconnect4.model.Game.WinType;
import java.util.Arrays;
import java.util.List;
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
        List<Pos> poses = Arrays.asList(
                new Pos(5, 0),
                new Pos(4, 0),
                new Pos(3, 0),
                new Pos(2, 0)
        );
        WinInfo expected = new WinInfo(
                Piece.P1, poses, WinType.COLUMN, 4
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
        List<Pos> poses = Arrays.asList(
                new Pos(5, 0),
                new Pos(5, 1),
                new Pos(5, 2),
                new Pos(5, 3)
        );
        WinInfo expected = new WinInfo(
                Piece.P1, poses, WinType.ROW, 4
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
        List<Pos> poses = Arrays.asList(
                new Pos(5, 0),
                new Pos(4, 1),
                new Pos(3, 2),
                new Pos(2, 3)
        );
        WinInfo expected = new WinInfo(
                Piece.P1, poses, WinType.DIAGONAL_ASC, 6
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
        List<Pos> poses = Arrays.asList(
                new Pos(2, 0),
                new Pos(3, 1),
                new Pos(4, 2),
                new Pos(5, 3)
        );
        WinInfo expected = new WinInfo(
                Piece.P1, poses, WinType.DIAGONAL_DESC, 6
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
