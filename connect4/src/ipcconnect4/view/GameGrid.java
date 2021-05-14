package ipcconnect4.view;

import static ipcconnect4.model.Game.COLUMNS;
import static ipcconnect4.model.Game.ROWS;
import ipcconnect4.model.Game.Piece;
import ipcconnect4.model.Game.Pos;
import ipcconnect4.util.Animation;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;

public class GameGrid extends GridPane {

    private static final int ANIMATION_DELAY = 100;
    private static final int RESET_DELAY = 100;
    private static final int ANIMATION_MAX_THREADS = 6;
    private static final float PREVIEW_OPACITY = 0.35F;

    private final BooleanProperty ended = new SimpleBooleanProperty(false);
    public final IntegerProperty pendingAnims = new SimpleIntegerProperty(0);
    private final ScheduledExecutorService resetExecutor
            = Executors.newSingleThreadScheduledExecutor(
                    runnable -> {
                        Thread t = new Thread(runnable);
                        t.setDaemon(true);
                        return t;
                    });
    private final ScheduledExecutorService animExecutor
            = Executors.newScheduledThreadPool(ANIMATION_MAX_THREADS,
                    runnable -> {
                        Thread t = new Thread(runnable);
                        t.setDaemon(true);
                        return t;
                    });

    public GameGrid() {
        disableProperty().bind(Bindings.or(
                Bindings.notEqual(pendingAnims, 0),
                ended
        ));
    }

    public int getColumn(double xPosition) {
        return getColumn(xPosition, true);
    }
    
    public int getColumn(double xPosition, boolean calculateOffset) {
        double xOffset = localToScene(getBoundsInLocal()).getMinX() + 0.5;
        xOffset = calculateOffset ? xOffset : 0;
        int column = (int) ((xPosition - xOffset) * COLUMNS / getWidth());
        column = column >= COLUMNS ? COLUMNS - 1 : column;
        return column;
    }

    public void updatePiece(Piece piece, Pos pos, boolean animate) {
        if (animate) {
            pendingAnims.set(pendingAnims.get() + 1);
            animatePiece(piece, new Pos(0, pos.column), pos, false);
        } else {
            Circle pieceC = createPiece(piece);
            removePiece(pos, false);
            add(pieceC, pos.column, pos.row);
        }
    }

    public void previewPiece(Piece piece, Pos pos, boolean animate) {
        Circle pieceC = createPiece(piece);
        if (animate) {
            new Animation(Animation.NORMAL).fade(pieceC, 0, PREVIEW_OPACITY).play();
        } else {
            pieceC.setOpacity(PREVIEW_OPACITY);
        }
        add(pieceC, pos.column, pos.row);
    }

    private void animatePiece(Piece piece, Pos iniPos, Pos finPos, boolean reset) {
        if (iniPos.row >= finPos.row) {
            if (reset) {
                removePiece(iniPos, false);
            } else {
                updatePiece(piece, finPos, false);
            }
            pendingAnims.set(pendingAnims.get() - 1);
        } else {
            removePiece(iniPos, false);
            Circle before = createPiece(Piece.NONE);
            Circle temp = createPiece(piece);
            add(temp, iniPos.column, iniPos.row);

            animExecutor.schedule(
                    () -> Platform.runLater(() -> {
                        getChildren().remove(temp);
                        add(before, iniPos.column, iniPos.row);
                        animatePiece(piece, new Pos(iniPos.row + 1, iniPos.column), finPos, reset);
                    }),
                    ANIMATION_DELAY,
                    TimeUnit.MILLISECONDS);
        }
    }

    private Circle createPiece(Piece piece) {
        Circle circle = new Circle();
        switch (piece) {
            case NONE:
                circle.getStyleClass().add("circle-piece-none");
                break;
            case P1:
                circle.getStyleClass().add("circle-piece-p1");
                break;
            case P2:
                circle.getStyleClass().add("circle-piece-p2");
                break;
        }
        circle.radiusProperty().bind(Bindings.min(
                Bindings.subtract(Bindings.divide(widthProperty(), COLUMNS * 2), 3),
                Bindings.subtract(Bindings.divide(heightProperty(), ROWS * 2), 3)
        ));
        return circle;
    }

    private Circle removePiece(Pos pos, boolean animate) {
        Circle firstFound = null;
        List<Node> childrens = getChildren();
        for (int i = 0; i < childrens.size(); i++) {
            Node piece = childrens.get(i);
            if (piece instanceof Circle
                    && GridPane.getRowIndex(piece) == pos.row
                    && GridPane.getColumnIndex(piece) == pos.column) {
                Circle pieceC = (Circle) piece;
                if (animate) {
                    Transition t = new Animation(Animation.NORMAL).fade(pieceC, piece.getOpacity(), 0);
                    t.setOnFinished((event) -> childrens.remove(piece));
                    t.play();
                } else {
                    childrens.remove(piece);
                    i--;
                }
                if (firstFound == null) {
                    firstFound = pieceC;
                }
            }
        }
        return firstFound;
    }

    public void finish(List<Pos> winPositions, Function<Pos, Piece> getPiece) {
        ended.set(true);
        afterAnimations(() -> {
            List<Node> childrens = getChildren();

            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLUMNS; j++) {
                    Pos aPo = new Pos(i, j);
                    removePiece(aPo, true);
                }
            }

            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLUMNS; j++) {
                    Pos aPo = new Pos(i, j);
                    Piece aPi = getPiece.apply(aPo);
                    if (!winPositions.contains(aPo)) {
                        previewPiece(aPi, aPo, true);
                    } else {
                        updatePiece(aPi, aPo, false);
                    }
                }
            }
        });
    }

    public void afterAnimations(Runnable action) {
        if (pendingAnims.get() == 0) {
            action.run();
        } else {
            pendingAnims.addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    if (pendingAnims.get() == 0) {
                        pendingAnims.removeListener(this);
                        action.run();
                    }
                }
            });
        }
    }

    public void emptyBottomHalf(Function<Pos, Piece> getPiece) {
        pendingAnims.set(pendingAnims.get() + 1);
        emptyBottomHalf(getPiece, new Pos(ROWS - 1, 0));
    }

    private void emptyBottomHalf(Function<Pos, Piece> getPiece, Pos pos) {
        if (pos.column >= COLUMNS) {
            pos.column = 0;
            pos.row--;
        }
        if (pos.row < 0) {
            pendingAnims.set(pendingAnims.get() - 1);
        } else {
            Pos finPos;
            if (pos.row < ROWS / 2) {
                finPos = new Pos((int) (ROWS / 2.0 + 0.5 + pos.row), pos.column);
                resetExecutor.schedule(
                        () -> {
                            Platform.runLater(() -> {
                                pendingAnims.set(pendingAnims.get() + 1);
                                animatePiece(getPiece.apply(pos), new Pos(pos), finPos, false);
                                pos.column++;
                                emptyBottomHalf(getPiece, new Pos(pos));
                            });
                        },
                        RESET_DELAY,
                        TimeUnit.MILLISECONDS);
            } else {
                finPos = new Pos(ROWS, pos.column);
                resetExecutor.schedule(
                        () -> {
                            Platform.runLater(() -> {
                                pendingAnims.set(pendingAnims.get() + 1);
                                animatePiece(getPiece.apply(pos), new Pos(pos), finPos, true);
                                pos.column++;
                                emptyBottomHalf(getPiece, new Pos(pos));
                            });
                        },
                        RESET_DELAY,
                        TimeUnit.MILLISECONDS);
            }
        }
    }
}
