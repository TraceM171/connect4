package ipcconnect4.util;

import ipcconnect4.model.MovementAI;
import ipcconnect4.model.GameWithAI;
import java.util.LinkedList;
import java.util.Random;

public class MinMax {

    private final int maxDepth;
    public final Long seed;

    public MinMax(int maxDepth) {
        this(maxDepth, null);
    }
    
    public MinMax(int maxDepth, Long seed) {
        this.maxDepth = maxDepth;
        this.seed = seed;
    }

    public MovementAI getNextMove(GameWithAI game) {
        return max(new GameWithAI(game), 0);
    }

    public MovementAI min(GameWithAI game, int depth) { //MIN plays P1 (user)
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

    public MovementAI max(GameWithAI game, int depth) { //MAX plays P2
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
    
    private Random getRandom() {
        if (seed != null) {
            return new Random(seed);
        } else {
            return new Random();
        }
    }
}
