/*
Skrevet af: Frederik Hvarregaard Andersen 
Studienummer: s224801
*/

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

    private static String rapFile = "resources/Rapgame.wav";
    private static AudioClip rap = new AudioClip(ClassLoader.getSystemResource(rapFile).toString());
    
    private static ArrayList<Integer> activeAnimations = new ArrayList<Integer>();
    private static int nextId = 0;

    private static int getNextId() {
        // 32 = (12 - 2) * 3 + 1, corresponding to the maximum number of flips a single move can make, in 12x12, which is the largest boardSize
        nextId = (nextId + 1) & 31;
        return nextId;
    }

    public static void playSound() {
        sound.setCycleCount(2);
        sound.play();
    }

    public static void playRap() {
        rap.stop();
        rap.play();
    }

    public static void stopRap() {
        rap.stop();
    }

    public static boolean isAnimating() {
        // We are still animation if all animations haven't finished, and removed their ID from the list
        return activeAnimations.size() != 0;
    }

    public static void flipPiece(Circle circle, int duration, Color from, Color to){
        // Retrieve animation ID and add to list of active animations
        int animationId = getNextId();
        activeAnimations.add(animationId);

        Duration time = new Duration(duration);
        RotateTransition rotate = new RotateTransition();
        rotate.setNode(circle);
        rotate.setByAngle(180);
        rotate.setAxis(Rotate.Y_AXIS);
        rotate.setDuration(time);
        // Remove the animations ID from the list of active animations on completion
        rotate.setOnFinished(e -> activeAnimations.remove(Integer.valueOf(animationId))); 
        
        FillTransition fill = new FillTransition(new Duration(1), circle, from, to);
        fill.setDelay(time.divide(2));
        
        StrokeTransition stroke = new StrokeTransition(new Duration(1), circle, Color.TRANSPARENT, Color.BLACK);
        stroke.setDelay(time.divide(2));
        // Play transitions
        rotate.play();
        fill.play();
        stroke.play();
    }

    public static void halfFlip(Circle circle, int duration, Color to) {
        // Retrieve animation ID and add to list of active animations
        int animationId = getNextId();
        activeAnimations.add(animationId);

        circle.setRotationAxis(Rotate.Y_AXIS);
        circle.setRotate(90);
        circle.setFill(to);
        circle.setStroke(Color.BLACK);

        RotateTransition rotate = new RotateTransition(new Duration(duration), circle);
        rotate.setByAngle(90);
        rotate.setAxis(Rotate.Y_AXIS);
        // Remove the animations ID from the list of active animations on completion
        rotate.setOnFinished(e -> activeAnimations.remove(Integer.valueOf(animationId)));

        rotate.play();
    }  
}