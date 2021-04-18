package ipcconnect4.view;

import ipcconnect4.model.Game;
import static ipcconnect4.model.Game.COLUMNS;
import ipcconnect4.model.Game.Piece;
import ipcconnect4.model.Game.Pos;
import static ipcconnect4.model.Game.ROWS;
import java.awt.Color;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

public class GameGrid extends GridPane {

    private static final int ANIMATION_DELAY = 100;
    private static final int ANIMATION_MAX_THREADS = 6;
    private static final float PREVIEW_OPACITY = 0.35F;

    private boolean ended;
    private final ScheduledExecutorService executor
            = Executors.newScheduledThreadPool(ANIMATION_MAX_THREADS,
                    runnable -> {
                        Thread t = new Thread(runnable);
                        t.setDaemon(true);
                        return t;
                    });

    public int getColumn(MouseEvent event) {
        double x = event.getSceneX();
        double xOffset = localToScene(getBoundsInLocal()).getMinX() + 0.5;
        int column = (int) ((x - xOffset) * Game.COLUMNS / getWidth());
        column = column >= Game.COLUMNS ? Game.COLUMNS - 1 : column;
        return column;
    }

    public void updatePiece(Piece piece, Pos pos, boolean animate) {
        if (animate) {
            animatePiece(piece, new Pos(0, pos.column), pos);
        } else {
            Circle pieceC = createPiece(piece);
            removePiece(pos);
            add(pieceC, pos.column, pos.row);
        }
    }

    public void previewPiece(Piece piece, Pos pos) {
        Circle pieceC = createPiece(piece);
        pieceC.setOpacity(PREVIEW_OPACITY);
        add(pieceC, pos.column, pos.row);
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
                circle.setStrokeWidth(2);
                circle.setStrokeType(StrokeType.INSIDE);
                circle.setStroke(Paint.valueOf("#800000"));
                break;
            case P2:
                circle.setFill(Paint.valueOf("#ffff00"));
                circle.setStrokeWidth(2);
                circle.setStrokeType(StrokeType.INSIDE);
                circle.setStroke(Paint.valueOf("#D4AA00"));
                break;
        }
        circle.radiusProperty().bind(Bindings.min(
                Bindings.subtract(Bindings.divide(widthProperty(), COLUMNS * 2), 3),
                Bindings.subtract(Bindings.divide(heightProperty(), ROWS * 2), 3)
        ));
        return circle;
    }

    private Circle removePiece(Pos pos) {
        Circle firstFound = null;
        List<Node> childrens = getChildren();
        for (int i = 0; i < childrens.size(); i++) {
            Node piece = childrens.get(i);
            if (piece instanceof Circle
                    && GridPane.getRowIndex(piece) == pos.row
                    && GridPane.getColumnIndex(piece) == pos.column) {
                Circle pieceC = (Circle) piece;
                childrens.remove(piece);
                i--;
                if (firstFound == null) {
                    firstFound = pieceC;
                }
            }
        }
        return firstFound;
    }

    public void finish() {
        ended = true;
        setDisable(true);
    }
}
