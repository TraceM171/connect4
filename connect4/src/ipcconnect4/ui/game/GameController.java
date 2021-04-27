package ipcconnect4.ui.game;

import DBAccess.Connect4DAOException;
import ipcconnect4.Main;
import static ipcconnect4.Main.rb;
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
import java.awt.MouseInfo;
import java.awt.Point;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.Connect4;
import model.Player;

public class GameController {

    private static final int AI_DELAY_MIN = 1000;
    private static final int AI_DELAY_MAX = 1500;
    private static final boolean ANIMATION = true;

    private final Player P1, P2;
    private final Game game;
    private final GameWithAI gameAI;
    private final boolean vsAI;
    private final Difficulty difficulty;

    @FXML
    private ImageView exitIB;
    @FXML
    private CircleImage avatarI1;
    @FXML
    private Label nickNameT1;
    @FXML
    private Node nickNameBack1;
    @FXML
    private Label pointsT1;
    @FXML
    private Label nickNameT2;
    @FXML
    private Node nickNameBack2;
    @FXML
    private Label pointsT2;
    @FXML
    private CircleImage avatarI2;
    @FXML
    private GameGrid gameGrid;
    @FXML
    private Node winPopUp;
    @FXML
    private Label winnerTitleL;
    @FXML
    private ImageView showWinPopUpIB;
    @FXML
    private CircleImage avatarIWi;
    @FXML
    private Label nickNameTWi;
    @FXML
    private Label pointsLWi;
    @FXML
    private Label totalPointsLWi;

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
            pointsT1.setText(Main.formatWLang("points", Main.player1.getPoints()));
        } else {
            Platform.runLater(() -> Main.goToAuthenticate(1));
        }

        if (vsAI) {
            String imagePath, name;
            switch (difficulty) {
                case EASY:
                    imagePath = "/resources/img/diff_1.png";
                    name = rb.getString("ai_easy");
                    pointsT2.setText(Main.formatWLang("points", 100));
                    break;
                case NORMAL:
                    imagePath = "/resources/img/diff_2.png";
                    name = rb.getString("ai_normal");
                    pointsT2.setText(Main.formatWLang("points", 1000));
                    break;
                case HARD:
                default:
                    imagePath = "/resources/img/diff_3.png";
                    name = rb.getString("ai_hard");
                    pointsT2.setText(Main.formatWLang("points", -2));
                    break;
            }
            avatarI2.setImage(new Image(imagePath));
            nickNameT2.setText(name);
        } else {
            if (P2 != null) {
                avatarI2.setImage(P2.getAvatar());
                nickNameT2.setText(P2.getNickName());
                pointsT2.setText(Main.formatWLang("points", Main.player2.getPoints()));
            } else {
                Platform.runLater(() -> Main.goToHome());
            }
        }
        setTurn((vsAI ? gameAI : game).getNextPiece());
    }

    @FXML
    private void putPieceAction(MouseEvent event) {
        int column = gameGrid.getColumn(event.getSceneX());
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
                            ThreadLocalRandom.current().nextInt(AI_DELAY_MIN, AI_DELAY_MAX + 1),
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
        int column = gameGrid.getColumn(event.getSceneX());
        setPreview(column);
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
                gameGrid.afterAnimations(() -> {
                    Point mouse = MouseInfo.getPointerInfo().getLocation();
                    setPreview(gameGrid.getColumn(gameGrid.screenToLocal(mouse.x, mouse.y).getX()));
                });
            }

            @Override
            public void onWin(WinInfo winInfo) {
                gameGrid.finish(winInfo.poses, (pos) -> game_.getPiece(pos));
                setTurn(Piece.NONE);

                Player winner, looser;
                Image winAvatar;
                String winNickName;
                String winnerStyleName;
                String nickNameSC = "label-nickname-";
                String avatarSC = "circle-image-";
                String titleSC = "win-title-";
                Label winnerPointsLabel;
                if (winInfo.origin == Piece.P1) {
                    winner = Main.player1;
                    winnerStyleName = "p1";
                    winNickName = nickNameT1.getText();
                    winAvatar = avatarI1.getImage();
                    looser = vsAI ? null : Main.player2;
                    winnerPointsLabel = pointsT1;
                } else {
                    winner = vsAI ? null : Main.player2;
                    winnerStyleName = "p2";
                    winNickName = nickNameT2.getText();
                    winAvatar = avatarI2.getImage();
                    looser = Main.player1;
                    winnerPointsLabel = pointsT2;
                }

                avatarIWi.setImage(winAvatar);
                nickNameTWi.setText(winNickName);
                nickNameTWi.getStyleClass().add(nickNameSC + winnerStyleName);
                avatarIWi.getStyleClass().add(avatarSC + winnerStyleName);
                winnerTitleL.getStyleClass().add(titleSC + winnerStyleName);

                int points = 0;
                try {
                    Connect4 c4 = Connect4.getSingletonConnect4();
                    if (vsAI) {
                        points = c4.getPointsAlone() * gameAI.difficulty.value;
                    } else {
                        points = c4.getPointsRound();
                        c4.regiterRound(LocalDateTime.now(), winner, looser);
                    }
                    if (winner != null) {
                        winner.plusPoints(points);
                    }
                } catch (Connect4DAOException ex) {
                    Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
                }

                final int points_ = points;
                if (winner != null) {
                    pointsLWi.setText("+" + Main.formatWLang("points", points));
                    IntegerProperty animationIP
                            = Animations.count(
                                    winner.getPoints() - points_,
                                    winner.getPoints()
                            );
                    winnerPointsLabel.textProperty().bind(Bindings.createStringBinding(
                            () -> Main.formatWLang("points", animationIP.get()),
                            animationIP));
                } else {
                    totalPointsLWi.setText(winnerPointsLabel.getText());
                }

                exitIB.setOnMouseClicked((event) -> Main.goToHome());

                Runnable showPU = () -> {
                    winPopUp.visibleProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue) {
                            Animations.fadeOut(showWinPopUpIB);
                            if (winner != null) {
                                IntegerProperty animationIP
                                        = Animations.count(
                                                winner.getPoints() - points_,
                                                winner.getPoints()
                                        );
                                totalPointsLWi.textProperty().bind(Bindings.createStringBinding(
                                        () -> Main.formatWLang("points", animationIP.get()),
                                        animationIP));
                            }
                        } else {
                            Animations.fadeIn(showWinPopUpIB);
                        }
                    });

                    Animations.fadeIn(winPopUp);
                };
                gameGrid.afterAnimations(showPU);
            }

            @Override
            public void onFull() {
                if (!game_.isOver()) {
                    Game lGame = new Game(game_);
                    gameGrid.emptyBottomHalf((pos) -> lGame.getPiece(pos));
                    game_.emptyBottomHalf();
                    gameGrid.afterAnimations(() -> {
                        Point mouse = MouseInfo.getPointerInfo().getLocation();
                        setPreview(gameGrid.getColumn(gameGrid.screenToLocal(mouse.x, mouse.y).getX()));
                    });
                }
            }
        });
    }

    private void setPreview(int column) {
        Game game_ = vsAI ? gameAI : game;
        if (game_.getFirstEmptyRow(column) == -1) {
            unpreviewAction(null);
        } else {
            previewPos.setValue(new Pos(game_.getFirstEmptyRow(column), column));
        }
    }

    private void setTurn(Piece piece) {
        switch (piece) {
            case P1:
                Animations.fade(avatarI1, avatarI1.getOpacity(), 1).play();
                Animations.fade(nickNameBack1, nickNameBack1.getOpacity(), 1).play();
                Animations.fade(avatarI2, avatarI2.getOpacity(), 0.5).play();
                Animations.fade(nickNameBack2, nickNameBack2.getOpacity(), 0.5).play();
                break;
            case P2:
                Animations.fade(avatarI1, avatarI1.getOpacity(), 0.5).play();
                Animations.fade(nickNameBack1, nickNameBack1.getOpacity(), 0.5).play();
                Animations.fade(avatarI2, avatarI2.getOpacity(), 1).play();
                Animations.fade(nickNameBack2, nickNameBack2.getOpacity(), 1).play();
                break;
            case NONE:
                Animations.fade(avatarI1, avatarI1.getOpacity(), 1).play();
                Animations.fade(nickNameBack1, nickNameBack1.getOpacity(), 1).play();
                Animations.fade(avatarI2, avatarI2.getOpacity(), 1).play();
                Animations.fade(nickNameBack2, nickNameBack2.getOpacity(), 1).play();
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
        alert.setTitle(Main.rb.getString("confirm"));
        alert.setHeaderText(Main.rb.getString("exitgame_header"));
        alert.setContentText(Main.rb.getString("exitgame_content"));

        Image iconImage = new Image(Main.class.getResourceAsStream("/resources/img/question.png"));
        ImageView icon = new ImageView(iconImage);
        icon.setFitHeight(50);
        icon.setFitWidth(50);
        alert.setGraphic(icon);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(iconImage);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Main.styleSheet);
        dialogPane.getStyleClass().add("dialog");

        ButtonType buttonTypeConfirm
                = new ButtonType(Main.rb.getString("confirm"), ButtonData.YES);
        ButtonType buttonTypeCancel
                = new ButtonType(Main.rb.getString("cancel"), ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonTypeCancel, buttonTypeConfirm);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeConfirm) {
            Main.goToHome();
        }
    }

}
