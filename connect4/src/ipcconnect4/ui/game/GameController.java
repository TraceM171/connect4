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
import ipcconnect4.view.CircleImage;
import ipcconnect4.view.GameGrid;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import model.Player;

public class GameController {

    private static final int AI_DELAY = 300;
    private static final boolean ANIMATION = true;

    private final Player P1, P2;
    private final Game game;
    private final GameWithAI gameAI;
    private final boolean vsAI;
    private final Difficulty difficulty;

    @FXML
    private CircleImage avatarI1;
    @FXML
    private Label nickNameT1;
    @FXML
    private Label nickNameT2;
    @FXML
    private CircleImage avatarI2;
    @FXML
    private GameGrid gameGrid;

    private final Property<Pos> previewPos = new SimpleObjectProperty<>(null);

    public GameController(Player P1, Player P2) {
        this.P1 = P1;
        this.P2 = P2;
        this.game = new Game();
        this.gameAI = null;
        this.vsAI = false;
        this.difficulty = null;
    }

    public GameController(Player P1, Difficulty difficulty) {
        this.P1 = P1;
        this.P2 = null;
        this.game = null;
        this.gameAI = new GameWithAI(difficulty);
        this.vsAI = true;
        this.difficulty = difficulty;
    }

    @FXML
    public void initialize() {
        initTopBar();
        bindToGame();
        bindPreview();
    }

    private void initTopBar() {
        if (P1 != null) {
            avatarI1.setImage(P1.getAvatar());
            nickNameT1.setText(P1.getNickName());
        } else {
            Platform.runLater(() -> Main.goToAuthenticate(1));
        }

        if (vsAI) {
            String imagePath, name;
            switch (difficulty) {
                case EASY:
                    imagePath = "/resources/img/diff_1.png";
                    name = "Easy AI";
                    break;
                case NORMAL:
                    imagePath = "/resources/img/diff_2.png";
                    name = "Normal AI";
                    break;
                case HARD:
                default:
                    imagePath = "/resources/img/diff_3.png";
                    name = "Hard AI";
                    break;
            }
            avatarI2.setImage(new Image(imagePath));
            nickNameT2.setText(name);
        } else {
            if (P2 != null) {
                avatarI2.setImage(P2.getAvatar());
                nickNameT2.setText(P2.getNickName());
            } else {
                Platform.runLater(() -> Main.goToHome());
            }
        }
        setTurn((vsAI ? gameAI : game).getNextPiece());
    }

    @FXML
    private void putPieceAction(MouseEvent event) {
        int column = gameGrid.getColumn(event);
        unpreviewAction(null);
        if (vsAI) {
            if (gameAI.canPutPiece(column)) {
                gameAI.putPiece(column);
                if (!gameAI.isOver()) {
                    gameGrid.pendingAnims.set(gameGrid.pendingAnims.get() + 1);
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
                                    gameGrid.pendingAnims.set(gameGrid.pendingAnims.get() - 1);
                                });
                            },
                            AI_DELAY,
                            TimeUnit.MILLISECONDS
                    );
                }
            }
        } else {
            game.putPiece(column);
        }
    }

    @FXML
    private void previewAction(MouseEvent event) {
        int column = gameGrid.getColumn(event);
        Game game_ = vsAI ? gameAI : game;
        if (game_.getFirstEmptyRow(column) == -1) {
            unpreviewAction(null);
        } else {
            previewPos.setValue(new Pos(game_.getFirstEmptyRow(column), column));
        }
    }

    @FXML
    private void unpreviewAction(MouseEvent event) {
        previewPos.setValue(null);
    }

    private void bindPreview() {
        previewPos.addListener((observable, oldValue, newValue) -> {
            Game game_ = vsAI ? gameAI : game;
            if (oldValue != null) {
                gameGrid.updatePiece(Piece.NONE, oldValue, false);
            }
            if (newValue != null) {
                gameGrid.previewPiece(game_.getNextPiece(), newValue);
            }
        });
    }

    private void bindToGame() {
        for (int row = 0; row < Game.ROWS; row++) {
            for (int col = 0; col < Game.COLUMNS; col++) {
                gameGrid.updatePiece(Piece.NONE, new Pos(row, col), false);
            }
        }
        Game game_ = vsAI ? gameAI : game;
        game_.setListener(new GameListener() {
            @Override
            public void onChange(Movement movement) {
                gameGrid.updatePiece(movement.piece, movement.pos, ANIMATION);
                setTurn(game_.getNextPiece());
            }

            @Override
            public void onWin(WinInfo winInfo) {
                gameGrid.finish(winInfo.poses, (pos) -> game_.getPiece(pos));
                setTurn(Piece.NONE);
            }

            @Override
            public void onFull() {
                if (!game_.isOver()) {
                    Game lGame = new Game(game_);
                    gameGrid.emptyBottomHalf((pos) -> lGame.getPiece(pos));
                    game_.emptyBottomHalf();
                }
            }
        });
    }

    private void setTurn(Piece piece) {
        switch (piece) {
            case P1:
                avatarI1.setOpacity(1);
                nickNameT1.setOpacity(1);
                avatarI2.setOpacity(0.5);
                nickNameT2.setOpacity(0.5);
                break;
            case P2:
                avatarI1.setOpacity(0.5);
                nickNameT1.setOpacity(0.5);
                avatarI2.setOpacity(1);
                nickNameT2.setOpacity(1);
                break;
            case NONE:
                avatarI1.setOpacity(1);
                nickNameT1.setOpacity(1);
                avatarI2.setOpacity(1);
                nickNameT2.setOpacity(1);
                break;
        }
    }

    @FXML
    private void exitAction(MouseEvent event) {
        Main.goToHome();
    }

}
