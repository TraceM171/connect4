package ipcconnect4.ui.game;

import ipcconnect4.Main;
import ipcconnect4.model.Game;
import ipcconnect4.model.Game.Piece;
import ipcconnect4.model.Game.Pos;
import ipcconnect4.model.Game.WinInfo;
import ipcconnect4.model.Movement;
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

    private final Player P1, P2;
    private final Game game;

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

        game.putPiece(column);
    }

    private void bindToGame() {
        for (int row = 0; row < Game.ROWS; row++) {
            for (int col = 0; col < Game.COLUMNS; col++) {
                updatePiece(new Pos(row, col));
            }
        }
        game.setListener(new Game.GameListener() {
            @Override
            public void onChange(Movement movement) {
                updatePiece(movement.pos);
            }

            @Override
            public void onWin(Game.WinInfo winInfo) {
                setWinner(winInfo);
            }
        });
    }

    private void updatePiece(Pos pos) {
        removePiece(pos);
        Circle piece = createPiece(game.getPiece(pos));
        gameGrid.add(piece, pos.column, pos.row);
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

    private void removePiece(Pos pos) {
        ObservableList<Node> childrens = gameGrid.getChildren();
        for (Node piece : childrens) {
            if (piece instanceof ImageView
                    && GridPane.getRowIndex(piece) == pos.row
                    && GridPane.getColumnIndex(piece) == pos.column) {
                gameGrid.getChildren().remove(piece);
                break;
            }
        }
    }
    
    private void setWinner(WinInfo winInfo) {
        gameGrid.setDisable(true);
    }

    @FXML
    private void exitAction(MouseEvent event) {
        Main.goToHome();
    }

}
