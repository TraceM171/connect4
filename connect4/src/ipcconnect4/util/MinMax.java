package ipcconnect4.util;

import ipcconnect4.model.Game;
import java.util.LinkedList;
import java.util.Random;

public class MinMax {

    //Variable that holds the maximum depth the MinMax algorithm will reach (level of the three)
    int maxDepth;

    public MinMax() {
        maxDepth = 5; //This is important to get a better decision (more depth, more accurate decision, more time)
    }

    //Initiates the MinMax algorithm
    public Movement getNextMove(Game game) {
        //We want to take the lowest of its recursive generated values in order to choose the greatest
        return max(Game.copyOf(game), 0);
    }

    //The max and min methods are called interchangingly, one after another until a max depth is reached
    public Movement min(Game game, int depth) { //MIN plays 'X' (user)
        Random r = new Random();
        // If MIN is called on a state that is terminal or after a maximum depth is reached, then a heuristic is calculated on the state and the move returned.
        if ((game.isOver()) || (depth == maxDepth)) {
            return new Movement(new Game.Pos(game.getLastMovement().pos.row, game.getLastMovement().pos.column), game.utilityFunction());
        } else {
            //The children-moves of the state are calculated (expansion)
            LinkedList<Game> children = new LinkedList<>(game.getChildren(Game.Piece.P1));
            Movement minMove = new Movement(Integer.MAX_VALUE);
            for (int i = 0; i < children.size(); i++) {
                Game child = children.get(i);
                //And for each child max is called, on a lower depth
                Movement move = max(child, depth + 1);
                //The child-move with the lowest value is selected and returned by max
                if (move.value <= minMove.value) {
                    if ((move.value == minMove.value)) {
                        //If the heuristic has the same value then we randomly choose one of the two moves
                        if (r.nextInt(2) == 0) { //If 0, we rewrite the maxMove. Else, the maxMove is the same
                            minMove.pos = child.getLastMovement().pos;
                            minMove.value = move.value;
                        }
                    } else {
                        minMove.pos = child.getLastMovement().pos;
                        minMove.value = move.value;
                    }
                }
            }
            return minMove;
        }
    }

    //The max and min methods are called interchangingly, one after another until a max depth or game over is reached
    public Movement max(Game game, int depth) { //MAX plays 'O'
        Random r = new Random();
        //If MAX is called on a state that is terminal or after a maximum depth is reached, then a heuristic is calculated on the state and the move returned.
        if ((game.isOver()) || (depth == maxDepth)) {
            return new Movement(new Game.Pos(game.getLastMovement().pos.row, game.getLastMovement().pos.column), game.utilityFunction());
        } else {
            LinkedList<Game> children = new LinkedList<>(game.getChildren(Game.Piece.P2));
            Movement maxMove = new Movement(Integer.MAX_VALUE);;
            for (int i = 0; i < children.size(); i++) {
                Game child = children.get(i);
                Movement move = min(child, depth + 1);
                //Here is the difference with Min method: The greatest value is selected
                if (move.value >= maxMove.value) {
                    if ((move.value == maxMove.value)) {
                        if (r.nextInt(2) == 0) {
                            maxMove.pos = child.getLastMovement().pos;
                            maxMove.value = move.value;
                        }
                    } else {
                        maxMove.pos = child.getLastMovement().pos;
                        maxMove.value = move.value;
                    }
                }
            }
            return maxMove;
        }
    }
}
