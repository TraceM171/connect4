package ipcconnect4.util;

import ipcconnect4.model.MovementAI;
import ipcconnect4.model.GameWithAI;
import java.util.LinkedList;
import java.util.Random;

// ***************************************
// * REFERENCES USED TO CREATE THIS CODE *
// ***************************************
// https://github.com/raulgonzalezcz/Connect4-AI-Java/blob/a7a8d9cd2b58d1c8eb4e2b68debf1789becac232/src/MinMax.java
// Used the minmax algorith developed by this user, and adapted it to use our model classes
//
/**
 * Class that uses the min max algorithm to find a good next movement on a
 * {@link Game}, can use the depth to control the quality of the movement, more
 * depth equals better choice but exponentially more time
 */
public class MinMax {

    private final int maxDepth;
    /**
     * Seed used in random movements
     */
    public final Long seed;

    /**
     * Creates a MinMax using {@link #MinMax(int, java.lang.Long)} with a random
     * seed
     *
     * @param maxDepth
     */
    public MinMax(int maxDepth) {
        this(maxDepth, null);
    }

    /**
     * Create a MinMax with the given algorithm depth and a seed to use in
     * random choices
     *
     * @param maxDepth
     * @param seed
     */
    public MinMax(int maxDepth, Long seed) {
        this.maxDepth = maxDepth;
        this.seed = seed;
    }

    /**
     * Get the MovementAI using the given game as a current game status
     *
     * @param game
     * @return movementAI
     */
    public MovementAI getNextMove(GameWithAI game) {
        return max(new GameWithAI(game), 0);
    }

    /**
     * ALGORITHM METHOD
     *
     * @param game
     * @param depth
     * @return
     */
    private MovementAI min(GameWithAI game, int depth) { //MIN plays P1 (user)
        Random r = getRandom();
        // If MIN is called on a state that is terminal or after a maximum depth is reached, then a heuristic is calculated on the state and the move returned.
        if (game.isOver() || depth == maxDepth) {
            MovementAI baseMove = new MovementAI(game.getLastMovement().pos, game.utilityFunction());
            return baseMove;
        } else {
            //The children-moves of the state are calculated (expansion)
            LinkedList<GameWithAI> children = new LinkedList<>(game.getChildren(GameWithAI.Piece.P1));
            MovementAI minMove = new MovementAI(Integer.MAX_VALUE);
            for (int i = 0; i < children.size(); i++) {
                GameWithAI child = children.get(i);
                //And for each child max is called, on a lower depth
                MovementAI move = max(child, depth + 1);
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

    /**
     * ALGORITHM METHOD
     *
     * @param game
     * @param depth
     * @return
     */
    private MovementAI max(GameWithAI game, int depth) { //MAX plays P2 (AI)
        Random r = getRandom();
        //If MAX is called on a state that is terminal or after a maximum depth is reached, then a heuristic is calculated on the state and the move returned.
        if ((game.isOver()) || (depth == maxDepth)) {
            MovementAI baseMove = new MovementAI(game.getLastMovement().pos, game.utilityFunction());
            return baseMove;
        } else {
            LinkedList<GameWithAI> children = new LinkedList<>(game.getChildren(GameWithAI.Piece.P2));
            MovementAI maxMove = new MovementAI(Integer.MIN_VALUE);
            for (int i = 0; i < children.size(); i++) {
                GameWithAI child = children.get(i);
                MovementAI move = min(child, depth + 1);
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

    /**
     * Get a random, will use this object seed if it is not null
     *
     * @return
     */
    private Random getRandom() {
        if (seed != null) {
            return new Random(seed);
        } else {
            return new Random();
        }
    }
}
