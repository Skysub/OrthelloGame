import java.util.ArrayList;

import javafx.animation.FillTransition;
import javafx.animation.RotateTransition;
import javafx.animation.StrokeTransition;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class Animation {
    
    private static String soundfile = "resources/PiecePlace.mp3";
    private static AudioClip sound = new AudioClip(ClassLoader.getSystemResource(soundfile).toString());
    

    private static ArrayList<Integer> activeAnimations = new ArrayList<Integer>();
    private static int nextId = 0;

    private static int getNextId() {
        nextId = (nextId + 1) & 43;  // 43 = (16 - 2) * 3 + 1, corresponding to the maximum number of flips a single move can make, in 16x16, which is the largest board
        return nextId;
    }

    public static void playSound() {
        sound.setCycleCount(2);
        sound.play();
    }

    public static boolean isAnimating() {
        return activeAnimations.size() != 0;
    }

    public static void flipPiece(Circle circle, int duration, Color from, Color to){

        int animationId = getNextId();
        activeAnimations.add(animationId);

        Duration time = new Duration(duration);
        RotateTransition rotate = new RotateTransition();
        rotate.setNode(circle);
        rotate.setByAngle(180);
        rotate.setAxis(Rotate.Y_AXIS);
        rotate.setDuration(time);
        rotate.setOnFinished(e -> activeAnimations.remove(Integer.valueOf(animationId))); 
        rotate.play();

        FillTransition fill = new FillTransition(new Duration(1), circle, from, to);
        fill.setDelay(time.divide(2));
        fill.play();

        StrokeTransition stroke = new StrokeTransition(new Duration(1), circle, Color.TRANSPARENT, Color.BLACK);
        stroke.setDelay(time.divide(2));
        stroke.play();
    }

    public static void halfFlip(Circle circle, int duration, Color to) {

        int animationId = getNextId();
        activeAnimations.add(animationId);

        circle.setRotationAxis(Rotate.Y_AXIS);
        circle.setRotate(90);
        circle.setFill(to);
        circle.setStroke(Color.BLACK);
        Duration time = new Duration(duration);
        RotateTransition rotate = new RotateTransition(time, circle);
        rotate.setByAngle(90);
        rotate.setAxis(Rotate.Y_AXIS);
        rotate.setOnFinished(e -> activeAnimations.remove(Integer.valueOf(animationId)));
        rotate.play();
    }  
}