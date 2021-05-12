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
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * Utility class used to make different animations.
 */
public class Animation {

    /**
     * Value = 300
     */
    public static final int NORMAL = 300;
    /**
     * Value = {@link #NORMAL} * 3. 3 times slower
     */
    public static final int SLOW = NORMAL * 3;
    /**
     * Value = {@link #NORMAL} / 3. 3 times faster
     */
    public static final int FAST = NORMAL / 3;

    private final Duration DURATION;

    public Animation(int millis) {
        this.DURATION = Duration.millis(millis);
    }

    public void slideFromTop(Pane container, Node oldNode, Node newNode) {
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

    public void slideToTop(Pane container, Node oldNode, Node newNode) {
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

    public void drawerHide(Pane container, Pane big, Pane small) {
        fadeOut(big);
        KeyValue keyValue = new KeyValue(big.prefWidthProperty(), small.getPrefWidth(), Interpolator.EASE_IN);
        KeyFrame keyFrame = new KeyFrame(DURATION, keyValue);
        Timeline timeline = new Timeline(keyFrame);
        timeline.setOnFinished(evt -> {
            big.setVisible(false);
            big.setManaged(false);
        });
        timeline.play();
    }

    public void drawerShow(Pane container, Pane big, double bigSize) {
        big.setVisible(true);
        big.setManaged(true);
        fadeIn(big);
        KeyValue keyValue = new KeyValue(big.prefWidthProperty(), bigSize, Interpolator.EASE_IN);
        KeyFrame keyFrame = new KeyFrame(DURATION, keyValue);
        Timeline timeline = new Timeline(keyFrame);
        timeline.play();
    }
    
    public void appearingSpin(Node node) {
        fadeIn(node);
        spin(node);
        fadeOut(node);
    }
    
    public void spin(Node node) {
        KeyValue keyValue = new KeyValue(node.rotateProperty(), 360, Interpolator.EASE_IN);
        KeyFrame keyFrame = new KeyFrame(DURATION, keyValue);
        Timeline timeline = new Timeline(keyFrame);
        timeline.setOnFinished(evt -> {
            node.setRotate(0);
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
    public void fadeIn(Pane container, Node oldNode, Node newNode) {
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
    public void fadeIn(Node node) {
        fade(node, 0, 1).play();
    }

    /**
     * Apply a fade out animation to a node, and then hide it
     *
     * @param node Node to be animated
     */
    public void fadeOut(Node node) {
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
    public Transition fade(Node node, double from, double to) {
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
     * amount of time, {@link #DURATION}.
     *
     * @param from int, from value
     * @param to int, to value
     * @return IntegerProperty, used to listen to changes in the value
     */
    public IntegerProperty count(int from, int to) {
        int amount = Math.abs(to - from);
        Duration kfDuration = DURATION.divide(amount);

        IntegerProperty ip = new SimpleIntegerProperty(from);
        Timeline timeline = new Timeline();
        timeline.setCycleCount(amount);
        timeline.getKeyFrames().add(
                new KeyFrame(kfDuration, (event) -> {
                    ip.set(ip.get() + (to > from ? 1 : -1));
                }));
        timeline.playFromStart();

        return ip;
    }

    public void listScrollTo(ScrollBar scrollBar, int items, int index) {
        double actualPos = (scrollBar.getValue() * 100);
        double finalPos = (scrollBar.getMax() - scrollBar.getMin()) / (items - 1) * index * 100;
        count((int) actualPos, (int) finalPos)
                .addListener((observable, oldValue, newValue) -> {
                    scrollBar.setValue(newValue.doubleValue() / 100);
                });
    }
}
