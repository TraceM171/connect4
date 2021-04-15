package ipcconnect4.view;

import static ipcconnect4.model.Game.COLUMNS;
import ipcconnect4.model.Game.Piece;
import ipcconnect4.model.Game.Pos;
import static ipcconnect4.model.Game.ROWS;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class GameGrid extends GridPane {
    
    private static final int ANIMATION_DELAY = 100;
    private static final int ANIMATION_MAX_THREADS = 6;
    
    private boolean ended;
    private final ScheduledExecutorService executor
            = Executors.newScheduledThreadPool(ANIMATION_MAX_THREADS,
                    runnable -> {
                        Thread t = new Thread(runnable);
                        t.setDaemon(true);
                        return t;
                    });

    public void updatePiece(Piece piece, Pos pos, boolean animate) {
        if (animate) {
            animatePiece(piece, new Pos(0, pos.column), pos);
        } else {
            Circle pieceC = createPiece(piece);
            removePiece(pos);
            add(pieceC, pos.column, pos.row);
        }
    }

    private void animatePiece(Piece piece, Pos iniPos, Pos finPos) {
        if (iniPos.row >= finPos.row) {
            updatePiece(piece, finPos, false);
            setDisable(ended);
        } else {
            Circle before = removePiece(iniPos);
            Circle temp = createPiece(piece);
            add(temp, iniPos.column, iniPos.row);
            setDisable(true);

            executor.schedule(
                    () -> Platform.runLater(() -> {
                        getChildren().remove(temp);
                        add(before, iniPos.column, iniPos.row);
                        animatePiece(piece, new Pos(iniPos.row + 1, iniPos.column), finPos);
                    }),
                    ANIMATION_DELAY,
                    TimeUnit.MILLISECONDS);
        }
    }

    private Circle createPiece(Piece piece) {
        Circle circle = new Circle();
        switch (piece) {
            case NONE:
                circle.setFill(Paint.valueOf("#ffffff"));
                break;
            case P1:
                circle.setFill(Paint.valueOf("#ff0000"));
                break;
            case P2:
                circle.setFill(Paint.valueOf("#ffff00"));
                break;
        }
        circle.radiusProperty().bind(Bindings.min(
                Bindings.subtract(Bindings.divide(widthProperty(), COLUMNS * 2), 3),
                Bindings.subtract(Bindings.divide(heightProperty(), ROWS * 2), 3)
        ));
        return circle;
    }

    private Circle removePiece(Pos pos) {
        ObservableList<Node> childrens = getChildren();
        for (Node piece : childrens) {
            if (piece instanceof Circle
                    && GridPane.getRowIndex(piece) == pos.row
                    && GridPane.getColumnIndex(piece) == pos.column) {
                Circle pieceC = (Circle) piece;
                getChildren().remove(piece);
                return pieceC;
            }
        }
        return null;
    }

    public void finish() {
        ended = true;
        setDisable(true);
    }
}
