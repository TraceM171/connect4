package ipcconnect4.ui.game;

import DBAccess.Connect4DAOException;
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
import ipcconnect4.util.Animations;
import ipcconnect4.view.CircleImage;
import ipcconnect4.view.GameGrid;
import ipcconnect4.view.IconButton;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import model.Connect4;
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
    private IconButton exitIB;
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
    @FXML
    private GridPane winPopUp;
    @FXML
    private IconButton showWinPopUpIB;
    @FXML
    private CircleImage avatarIWi;
    @FXML
    private Label nickNameTWi;
    @FXML
    private Label pointsLWi;
    @FXML
    private CircleImage avatarILo;
    @FXML
    private Label nickNameTLo;
    @FXML
    private Label pointsLLo;

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
                gameGrid.previewPiece(game_.getNextPiece(), newValue, false);
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

                Player winner, looser;
                Paint winColor, losColor;
                String winStyle, losStyle;
                if (winInfo.origin == Piece.P1) {
                    winner = Main.player1;
                    winColor = Paint.valueOf("#ff5b5b");
                    winStyle = nickNameT1.getStyle();
                    looser = Main.player2;
                    losColor = Paint.valueOf("#ffd951");
                    losStyle = nickNameT2.getStyle();
                } else {
                    winner = Main.player2;
                    winColor = Paint.valueOf("#ffd951");
                    winStyle = nickNameT2.getStyle();
                    looser = Main.player1;
                    losColor = Paint.valueOf("#ff5b5b");
                    losStyle = nickNameT1.getStyle();
                }

                avatarIWi.setImage(winner.getAvatar());
                avatarIWi.setStroke(winColor);
                nickNameTWi.setText(winner.getNickName());
                nickNameTWi.setStyle(winStyle);
                pointsLWi.setText("+5 Puntos");

                avatarILo.setImage(looser.getAvatar());
                avatarILo.setStroke(losColor);
                nickNameTLo.setText(looser.getNickName());
                nickNameTLo.setStyle(losStyle);
                pointsLLo.setText("-5 Puntos");

                try {
                    Connect4.getSingletonConnect4().regiterRound(LocalDateTime.now(), winner, looser);
                } catch (Connect4DAOException ex) {
                    Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                exitIB.setOnMouseClicked((event) -> Main.goToHome());

                Runnable showPU = () -> {
                    Animations.fadeIn(winPopUp);

                    winPopUp.visibleProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue) {
                            Animations.fadeOut(showWinPopUpIB);
                        } else {
                            Animations.fadeIn(showWinPopUpIB);
                        }
                    });
                };
                gameGrid.afterAnimations(showPU);
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
                Animations.fade(avatarI1, avatarI1.getOpacity(), 1).play();
                Animations.fade(nickNameT1, nickNameT1.getOpacity(), 1).play();
                Animations.fade(avatarI2, avatarI2.getOpacity(), 0.5).play();
                Animations.fade(nickNameT2, nickNameT2.getOpacity(), 0.5).play();
                break;
            case P2:
                Animations.fade(avatarI1, avatarI1.getOpacity(), 0.5).play();
                Animations.fade(nickNameT1, nickNameT1.getOpacity(), 0.5).play();
                Animations.fade(avatarI2, avatarI2.getOpacity(), 1).play();
                Animations.fade(nickNameT2, nickNameT2.getOpacity(), 1).play();
                break;
            case NONE:
                Animations.fade(avatarI1, avatarI1.getOpacity(), 1).play();
                Animations.fade(nickNameT1, nickNameT1.getOpacity(), 1).play();
                Animations.fade(avatarI2, avatarI2.getOpacity(), 1).play();
                Animations.fade(nickNameT2, nickNameT2.getOpacity(), 1).play();
                break;
        }
    }

    @FXML
    private void closeWinPopup(MouseEvent event) {
        Animations.fadeOut(winPopUp);
    }

    @FXML
    private void showWinPopUp(MouseEvent event) {
        Animations.fadeIn(winPopUp);
    }

    @FXML
    private void exitAction(MouseEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmar");
        alert.setHeaderText("Seguro que quieres salir?");
        alert.setContentText("Si sales de la partida perderás el progreso.");
        
        Image iconImage = new Image(Main.class.getResourceAsStream("/resources/img/question.png"));
        ImageView icon = new ImageView(iconImage);
        icon.setFitHeight(50);
        icon.setFitWidth(50);
        alert.setGraphic(icon);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(iconImage);

        ButtonType buttonTypeConfirm = new ButtonType("Confirmar", ButtonData.YES);
        ButtonType buttonTypeCancel = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeCancel, buttonTypeConfirm);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeConfirm) {
            Main.goToHome();
        }
    }

}
