package ipcconnect4.ui.game;

import ipcconnect4.Main;
import ipcconnect4.model.Game;
import ipcconnect4.model.Game.GameListener;
import ipcconnect4.model.Game.Piece;
import ipcconnect4.model.Game.Pos;
import ipcconnect4.model.Game.WinInfo;
import ipcconnect4.model.GameWithAI;
import ipcconnect4.model.GameWithAI.Difficulty;
import ipcconnect4.model.Movement;
import ipcconnect4.model.MovementAI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import model.Player;

public class GameController {

    private static final int ANIMATION_DELAY = 100;
    private static final int ANIMATION_MAX_THREADS = 6;
    private static final int AI_DELAY = 300;

    private final Player P1, P2;
    private final Game game;
    private final GameWithAI gameAI;
    private final boolean vsAI;
    private final ScheduledExecutorService executor
            = Executors.newScheduledThreadPool(ANIMATION_MAX_THREADS,
                    runnable -> {
                        Thread t = new Thread(runnable);
                        t.setDaemon(true);
                        return t;
                    });

    @FXML
    private ImageView avatarIV1;
    @FXML
    private Label nickNameT1;
    @FXML
    private Label nickNameT2;
    @FXML
    private ImageView avatarIV2;
    @FXML
    private GridPane gameGrid;

    public GameController(Player P1, Player P2) {
        this.P1 = P1;
        this.P2 = P2;
        this.game = new Game();
        this.gameAI = null;
        this.vsAI = false;
    }

    public GameController(Player P1, Difficulty difficulty) {
        this.P1 = P1;
        this.P2 = null;
        this.game = null;
        this.gameAI = new GameWithAI(difficulty);
        this.vsAI = true;
    }

    @FXML
    public void initialize() {
        initTopBar();
        bindToGame();
    }

    private void initTopBar() {
        if (P1 != null) {
            avatarIV1.setImage(P1.getAvatar());
            nickNameT1.setText(P1.getNickName());
        } else {
            Platform.runLater(() -> Main.goToAuthenticate(1));
        }

        if (P2 != null) {
            avatarIV2.setImage(P2.getAvatar());
            nickNameT2.setText(P2.getNickName());
        } else {
            avatarIV2.setImage(new Image("/avatars/default.png"));
            nickNameT2.setText("Computer");
        }
    }

    @FXML
    public void putPieceAction(MouseEvent event) {
        double x = event.getSceneX();
        double xOffset = gameGrid.localToScene(gameGrid.getBoundsInLocal()).getMinX();
        int column = (int) ((x - xOffset) * Game.COLUMNS / gameGrid.getWidth());

        if (vsAI) {
            if (gameAI.canPutPiece(column)) {
                gameAI.putPiece(column);
                ScheduledExecutorService executorAI = Executors.newSingleThreadScheduledExecutor(
                        runnable -> {
                            Thread t = new Thread(runnable);
                            t.setDaemon(true);
                            return t;
                        });
                executorAI.schedule(
                        () -> {
                            MovementAI movementAI = gameAI.getNextAIMovement();
                            Platform.runLater(() -> {
                                gameAI.putPiece(movementAI.pos.column);
                            });
                        },
                        AI_DELAY,
                        TimeUnit.MILLISECONDS
                );
            }
        } else {
            game.putPiece(column);
        }
    }

    private void bindToGame() {
        for (int row = 0; row < Game.ROWS; row++) {
            for (int col = 0; col < Game.COLUMNS; col++) {
                updatePiece(new Pos(row, col), false);
            }
        }
        GameListener listener = new GameListener() {
            @Override
            public void onChange(Movement movement) {
                updatePiece(movement.pos, true);
            }

            @Override
            public void onWin(WinInfo winInfo) {
                setWinner(winInfo);
            }
        };
        if (vsAI) {
            gameAI.setListener(listener);
        } else {
            game.setListener(listener);
        }
    }

    private void updatePiece(Pos pos, boolean animate) {
        Piece piece;
        if (vsAI) {
            piece = gameAI.getPiece(pos);
        } else {
            piece = game.getPiece(pos);
        }
        if (animate) {
            animatePiece(piece, new Pos(0, pos.column), pos);
        } else {
            Circle pieceC = createPiece(piece);
            removePiece(pos);
            gameGrid.add(pieceC, pos.column, pos.row);
        }
    }

    private void animatePiece(Piece piece, Pos iniPos, Pos finPos) {
        if (iniPos.row >= finPos.row) {
            updatePiece(finPos, false);
            gameGrid.setDisable(false);
        } else {
            Circle before = removePiece(iniPos);
            Circle temp = createPiece(piece);
            gameGrid.add(temp, iniPos.column, iniPos.row);
            gameGrid.setDisable(true);

            executor.schedule(
                    () -> Platform.runLater(() -> {
                        gameGrid.getChildren().remove(temp);
                        gameGrid.add(before, iniPos.column, iniPos.row);
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
                Bindings.subtract(Bindings.divide(gameGrid.widthProperty(), Game.COLUMNS * 2), 3),
                Bindings.subtract(Bindings.divide(gameGrid.heightProperty(), Game.ROWS * 2), 3)
        ));
        return circle;
    }

    private Circle removePiece(Pos pos) {
        ObservableList<Node> childrens = gameGrid.getChildren();
        for (Node piece : childrens) {
            if (piece instanceof Circle
                    && GridPane.getRowIndex(piece) == pos.row
                    && GridPane.getColumnIndex(piece) == pos.column) {
                Circle pieceC = (Circle) piece;
                gameGrid.getChildren().remove(piece);
                return pieceC;
            }
        }
        return null;
    }

    private void setWinner(WinInfo winInfo) {
        gameGrid.setDisable(true);
    }

    @FXML
    private void exitAction(MouseEvent event) {
        Main.goToHome();
    }

}
