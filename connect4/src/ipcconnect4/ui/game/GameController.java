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
import ipcconnect4.view.GameGrid;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import model.Player;

public class GameController {

    private static final int AI_DELAY = 300;

    private final Player P1, P2;
    private final Game game;
    private final GameWithAI gameAI;
    private final boolean vsAI;

    @FXML
    private ImageView avatarIV1;
    @FXML
    private Label nickNameT1;
    @FXML
    private Label nickNameT2;
    @FXML
    private ImageView avatarIV2;
    @FXML
    private GameGrid gameGrid;

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
            avatarIV2.setImage(new Image("/resources/img/ai.png"));
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
                gameGrid.updatePiece(Piece.NONE, new Pos(row, col), false);
            }
        }
        GameListener listener = new GameListener() {
            @Override
            public void onChange(Movement movement) {
                Piece piece;
                if (vsAI) {
                    piece = gameAI.getPiece(movement.pos);
                } else {
                    piece = game.getPiece(movement.pos);
                }
                gameGrid.updatePiece(piece, movement.pos, true);
            }

            @Override
            public void onWin(WinInfo winInfo) {
                gameGrid.finish();
            }
        };
        if (vsAI) {
            gameAI.setListener(listener);
        } else {
            game.setListener(listener);
        }
    }

    @FXML
    private void exitAction(MouseEvent event) {
        Main.goToHome();
    }

}
