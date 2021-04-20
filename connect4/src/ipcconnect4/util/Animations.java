package ipcconnect4.util;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class Animations {

    private static final Duration DURATION = Duration.millis(300);

    public static void slideFromRight(Pane container, Node oldNode, Node newNode) {
        newNode.translateXProperty().set(container.getWidth());
        container.getChildren().add(newNode);

        KeyValue keyValue = new KeyValue(newNode.translateXProperty(), 0, Interpolator.EASE_IN);
        KeyFrame keyFrame = new KeyFrame(DURATION, keyValue);
        Timeline timeline = new Timeline(keyFrame);
        timeline.setOnFinished(evt -> {
            if (oldNode != null) {
                container.getChildren().remove(oldNode);
            }
        });
        timeline.play();
    }

    public static void slideToRight(Pane container, Node oldNode, Node newNode) {
        if (oldNode == null) {
            return;
        }

        container.getChildren().remove(oldNode);
        container.getChildren().add(newNode);
        container.getChildren().add(oldNode);

        KeyValue keyValue = new KeyValue(oldNode.translateXProperty(), container.getWidth(), Interpolator.EASE_IN);
        KeyFrame keyFrame = new KeyFrame(DURATION, keyValue);
        Timeline timeline = new Timeline(keyFrame);
        timeline.setOnFinished(evt -> {
            container.getChildren().remove(oldNode);
        });
        timeline.play();
    }

    public static void fadeIn(Pane container, Node oldNode, Node newNode) {
        container.getChildren().add(newNode);

        FadeTransition ft = new FadeTransition(DURATION);

        ft.setOnFinished(evt -> {
            container.getChildren().remove(oldNode);
        });
        ft.setNode(newNode);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    public static void fadeIn(Node node) {
        fade(node, 0, 1).play();
    }

    public static void fadeOut(Node node) {
        Transition ft = fade(node, 1, 0);
        ft.setOnFinished(evt -> {
            node.setVisible(false);
        });
        ft.play();
    }
    
    public static Transition fade(Node node, double from, double to) {
        node.setOpacity(from);
        node.setVisible(true);
        FadeTransition ft = new FadeTransition(DURATION);
        ft.setNode(node);
        ft.setFromValue(from);
        ft.setToValue(to);
        return ft;
    }
}
