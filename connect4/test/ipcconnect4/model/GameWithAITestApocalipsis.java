package ipcconnect4.model;

import static ipcconnect4.model.Game.COLUMNS;
import ipcconnect4.model.Game.Pos;
import static ipcconnect4.model.Game.ROWS;
import static ipcconnect4.model.GameWithAITestCLI.printGame;
import java.util.Random;
import main.State;
import main.MinMax;
import java.util.Scanner;
import main.GamePlay;
import org.junit.Test;
import static org.junit.Assert.*;

public class GameWithAITestApocalipsis {

    @Test
    public static void main(String[] args) {
        // Seeds
        Long SEED1 = new Random().nextLong();
        //-
        Long SEED2 = new Random().nextLong();

        // Init games
        GameWithAI game1 = new GameWithAI(GameWithAI.Difficulty.HARD, SEED1);
        MinMax computerPlayer1 = new MinMax(State.O, SEED1);
        State theBoard1 = new State();
        //-
        GameWithAI game2 = new GameWithAI(GameWithAI.Difficulty.HARD, SEED2);
        MinMax computerPlayer2 = new MinMax(State.O, SEED2);
        State theBoard2 = new State();

        // Initial print
        System.out.println("Q empiese el juego!\n");
        printGame(game1);
        
        while (!game1.isOver()) {
            switch (game1.getNextPiece()) {
                case P1:
                    
                    Movement ours2 = game2.getNextAIMovement();
                    GamePlay theirs2 = computerPlayer2.getNextMove(theBoard2);
                    assertEquals(new Pos(theirs2.row, theirs2.col), ours2.pos);
                    
                    game1.putPiece(ours2.pos.column, Game.Piece.P1);
                    theBoard1.makeMove(ours2.pos.column, State.X);
                    
                    game2.putPiece(ours2.pos.column, Game.Piece.P2);
                    theBoard2.makeMove(ours2.pos.column, State.O);

                    System.out.println("Computers '\u001B[34mX\u001B[30m' moves on column " + (game1.getLastMovement().pos.column + 1) + ".");
                    System.out.println();
                    break;
                    
                case P2:
                    
                    Movement ours1 = game1.getNextAIMovement();
                    GamePlay theirs1 = computerPlayer1.getNextMove(theBoard1);
                    assertEquals(new Pos(theirs1.row, theirs1.col), ours1.pos);
                    
                    game2.putPiece(ours1.pos.column, Game.Piece.P1);
                    theBoard2.makeMove(ours1.pos.column, State.X);
                    
                    game1.putPiece(ours1.pos.column, Game.Piece.P2);
                    theBoard1.makeMove(ours1.pos.column, State.O);

                    System.out.println("Computers '\u001B[31mO\u001B[30m' moves on column " + (game1.getLastMovement().pos.column + 1) + ".");
                    System.out.println();
                    break;
                    
                default:
                    break;
            }
            printGame(game1);
        }
    }

}
