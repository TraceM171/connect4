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

    /**
     * Create an object whose animations use the provided duration
     *
     * @param millis int, time in millis as base for all animations
     */
    public Animation(int millis) {
        this.DURATION = Duration.millis(millis);
    }

    /**
     * Transition between two nodes using slide from top animation animation
     *
     * @param container Pane, parent of oldNode and will also be parent of
     * newNode
     * @param oldNode Node, old node that will stay still and then disappear
     * @param newNode Node, new node that will come from top to take the
     * position of oldNode
     */
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

    /**
     * Transition between two nodes using slide from top animation animation
     *
     * @param container Pane, parent of oldNode and will also be parent of
     * newNode
     * @param oldNode Node, old node that will slide to the top of container and
     * then disappear
     * @param newNode Node, new node that will appear on the original position
     * of oldNode
     */
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

    /**
     * Hides the big drawer and shows the small one. Fade out and slide to left
     * the big drawer until it is visible no more. Making visible the small one
     * that is always on the background
     *
     * @param container Pane, parent of big and small drawers
     * @param big Pane, big drawer
     * @param small Pane, small drawer
     */
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

    /**
     * Shows the big drawer and hides the small one. Fade in and slide from left
     * the big drawer until its width is the same as given bigSize. Hiding the
     * small behind the big one.
     *
     * @param container Pane, parent of big and small drawers
     * @param big Pane, big drawer
     * @param bigSize double, size that the big drawer should have
     */
    public void drawerShow(Pane container, Pane big, double bigSize) {
        big.setVisible(true);
        big.setManaged(true);
        fadeIn(big);
        KeyValue keyValue = new KeyValue(big.prefWidthProperty(), bigSize, Interpolator.EASE_IN);
        KeyFrame keyFrame = new KeyFrame(DURATION, keyValue);
        Timeline timeline = new Timeline(keyFrame);
        timeline.play();
    }

    /**
     * Make a node spin while fading in and out
     *
     * @param node Node, to animate
     */
    public void appearingSpin(Node node) {
        fadeIn(node);
        spin(node);
        fadeOut(node);
    }

    /**
     * Make the node go one full turn clockwise, returning to its starting
     * position
     *
     * @param node Node, to animate
     */
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

    /**
     * Smooth scroll to a given index in a list, and the number of items it has
     *
     * @param scrollBar ScrollBar, target of the smooth scroll
     * @param items int, size of the list
     * @param index int, index of the item in the list to scroll to
     */
    public void listScrollTo(ScrollBar scrollBar, int items, int index) {
        double actualPos = (scrollBar.getValue() * 100);
        double finalPos = (scrollBar.getMax() - scrollBar.getMin()) / (items - 1) * index * 100;
        count((int) actualPos, (int) finalPos)
                .addListener((observable, oldValue, newValue) -> {
                    scrollBar.setValue(newValue.doubleValue() / 100);
                });
    }
}
