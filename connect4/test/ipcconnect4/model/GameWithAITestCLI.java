package ipcconnect4.model;

import static ipcconnect4.model.Game.COLUMNS;
import static ipcconnect4.model.Game.ROWS;
import java.util.Random;
import java.util.Scanner;

public class GameWithAITestCLI {

    private static Long SEED = null;

    public static void main(String[] args) {
        SEED = new Random().nextLong();
        GameWithAI game = new GameWithAI(GameWithAI.Difficulty.NORMAL, SEED);
        int columnPosition;
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
                    System.out.println();
                    break;
                case P2:
                    game.performAIMovement();
                    System.out.println("Computer '\u001B[31mO\u001B[30m' moves on column " + (game.getLastMovement().pos.column + 1) + ".");
                    System.out.println();
                    break;
                default:
                    break;
            }
            printGame(game);
        }
    }

    static void printGame(Game game) {
        System.out.println("| 1 | 2 | 3 | 4 | 5 | 6 | 7 |");
        System.out.println();
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                switch (game.getPiece(new Game.Pos(i, j))) {
                    case P1:
                        System.out.print("| " + "\u001B[34mX" + "\u001B[30m "); //Blue for user
                        break;
                    case P2:
                        System.out.print("| " + "\u001B[31mO" + "\u001B[30m "); //Red for computer
                        break;
                    default:
                        System.out.print("| " + "-" + " ");
                        break;
                }
            }
            System.out.println("|");
        }
    }

}
