import java.io.File;

import javafx.animation.FillTransition;
import javafx.animation.RotateTransition;
import javafx.animation.StrokeTransition;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class Animation {
    
    private static String soundfile = "PiecePlace.mp3";
    private static AudioClip sound = new AudioClip(new File(soundfile).toURI().toString());

    public static void placePiece(Circle circle, Color color){
        circle.setFill(color);
        sound.play();
    }

    public static void playSound() {
        sound.play();
    }

    public static void flipPiece(Circle circle, int duration, Color from, Color to, Runnable onFinished){
        Duration time = new Duration(duration);
        RotateTransition rotate = new RotateTransition();
        rotate.setNode(circle);
        rotate.setByAngle(180);
        rotate.setAxis(Rotate.Y_AXIS);
        rotate.setDuration(time);
        rotate.setOnFinished(e -> onFinished.run());
        rotate.play();

        FillTransition fill = new FillTransition(new Duration(1), circle, from, to);
        fill.setDelay(time.divide(2));
        fill.play();

        StrokeTransition stroke = new StrokeTransition(new Duration(1), circle, Color.TRANSPARENT, Color.BLACK);
        stroke.setDelay(time.divide(2));
        stroke.play();
    }
}