package ipcconnect4.model;

import ipcconnect4.model.Game.Pos;
import static ipcconnect4.model.GameWithAITestCLI.printGame;
import java.util.Random;
import main.State;
import main.MinMax;
import java.util.Scanner;
import main.GamePlay;
import org.junit.Test;
import static org.junit.Assert.*;

public class GameWithAITestCompare {

    static int columnPosition;
    static State theBoard;
    static MinMax computerPlayer;

    private static Long SEED = null;

    @Test
    public static void main(String[] args) {
        SEED = new Random().nextLong();

        GameWithAI game = new GameWithAI(GameWithAI.Difficulty.HARD, SEED);
        computerPlayer = new MinMax(State.O, SEED);
        theBoard = new State();

        System.out.println("Connect 4 in Java!\n");
        printGame(game);
        //While the game has not finished
        while (!game.isOver()) {
            switch (game.getNextPiece()) {
                case P1:
                    System.out.print("User '\u001B[34mX\u001B[30m' moves.");
                    try {
                        do {
                            System.out.print("\nSelect a column to drop your piece (1-7): ");
                            Scanner in = new Scanner(System.in);
                            columnPosition = in.nextInt();
                        } while (!game.canPutPiece(columnPosition - 1));
                    } catch (Exception e) {
                        System.out.println("\nValid numbers are 1,2,3,4,5,6 or 7. Try again\n");
                        break;
                    }
                    //Movement of the user
                    game.putPiece(columnPosition - 1);
                    theBoard.makeMove(columnPosition - 1, State.X);

                    System.out.println();
                    break;
                case P2:
                    GamePlay theirs = computerPlayer.getNextMove(theBoard);
                    Movement ours = game.getNextAIMovement();
                    assertEquals(new Pos(theirs.row, theirs.col), ours.pos);
                    game.performAIMovement();
                    theBoard.makeMove(theirs.col, State.O);

                    System.out.println("Computer '\u001B[31mO\u001B[30m' moves on column " + (game.getLastMovement().pos.column + 1) + ".");
                    System.out.println();
                    break;
                default:
                    break;
            }
            printGame(game);
        }
    }

}
