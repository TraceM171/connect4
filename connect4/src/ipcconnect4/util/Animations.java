package ipcconnect4.util;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * Utility class used to make different animations.
 */
public class Animations {

    /**
     * Duration of all animations. Value = 300
     */
    public static final Duration DURATION = Duration.millis(300);

    public static void slideFromTop(Pane container, Node oldNode, Node newNode) {
        newNode.translateYProperty().set(-1 * container.getHeight());

        container.getChildren().add(newNode);

        KeyValue keyValue = new KeyValue(newNode.translateYProperty(), 0, Interpolator.EASE_IN);
        KeyFrame keyFrame = new KeyFrame(DURATION, keyValue);
        Timeline timeline = new Timeline(keyFrame);
        timeline.setOnFinished(evt -> {
            container.getChildren().remove(oldNode);
        });
        timeline.play();
    }
    
    public static void slideToTop(Pane container, Node oldNode, Node newNode) {
        container.getChildren().add(newNode);
        container.getChildren().remove(oldNode);
        container.getChildren().add(oldNode);

        KeyValue keyValue = new KeyValue(oldNode.translateYProperty(), -1 * container.getHeight(), Interpolator.EASE_IN);
        KeyFrame keyFrame = new KeyFrame(DURATION, keyValue);
        Timeline timeline = new Timeline(keyFrame);
        timeline.setOnFinished(evt -> {
            container.getChildren().remove(oldNode);
        });
        timeline.play();
    }

    /**
     * Transition between two nodes using fade animation
     *
     * @param container Pane, parent of oldNode and will also be parent of
     * newNode
     * @param oldNode Node, old node that will disappear
     * @param newNode Node, new node that will take the position of oldNode
     */
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

    /**
     * Apply a fade in animation to a node
     *
     * @param node Node to be animated
     */
    public static void fadeIn(Node node) {
        fade(node, 0, 1).play();
    }

    /**
     * Apply a fade out animation to a node, and then hide it
     *
     * @param node Node to be animated
     */
    public static void fadeOut(Node node) {
        Transition ft = fade(node, 1, 0);
        ft.setOnFinished(evt -> {
            node.setVisible(false);
        });
        ft.play();
    }

    /**
     * Create a fade transition used to animate nodes, will set visibility to
     * true
     *
     * @param node Node to be animated
     * @param from double, initial value
     * @param to double, final value
     * @return Transition, represents the fade animation
     */
    public static Transition fade(Node node, double from, double to) {
        node.setOpacity(from);
        node.setVisible(true);
        FadeTransition ft = new FadeTransition(DURATION);
        ft.setNode(node);
        ft.setFromValue(from);
        ft.setToValue(to);
        return ft;
    }

    /**
     * Create an IntegerProperty that will go from a value to another one, going
     * up or down one at a time. Will always end the animation in the same
     * amount of time, {@link #DURATION} * 3.
     *
     * @param from int, from value
     * @param to int, to value
     * @return IntegerProperty, used to listen to changes in the value
     */
    public static IntegerProperty count(int from, int to) {
        int amount = Math.abs(to - from);
        Duration kfDuration = DURATION.multiply(3).divide(amount);

        IntegerProperty ip = new SimpleIntegerProperty(from);
        Timeline timeline = new Timeline();
        timeline.setCycleCount(amount);
        timeline.getKeyFrames().add(
                new KeyFrame(kfDuration, (event) -> {
                    ip.set(ip.get() + 1);
                }));
        timeline.playFromStart();

        return ip;
    }
}
