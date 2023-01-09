import java.io.File;

import javafx.animation.FillTransition;
import javafx.animation.RotateTransition;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class Animation {

    public static void flipPiece(Circle circle, int duration, Controller controller){
        controller.isAnimating = true;
        Duration time = new Duration(duration);
        RotateTransition rotate = new RotateTransition();
        rotate.setNode(circle);
        rotate.setByAngle(180);
        rotate.setAxis(Rotate.Y_AXIS);
        rotate.setDuration(time);
        rotate.setOnFinished(event -> {
            controller.isAnimating = false;
        });
        rotate.play();


        Color now;
        Color after;
        if(circle.getFill() == Color.BLACK){
            now = Color.BLACK;
            after = Color.WHITE;
        }
        else{
            now = Color.WHITE;
            after = Color.BLACK; 
        }
        

        FillTransition fill = new FillTransition(new Duration(1), circle, now, after);
        fill.setDelay(time.divide(2));
        fill.play();
    }

    private static String soundfile = "PiecePlace.mp3";
    private static AudioClip sound = new AudioClip(new File(soundfile).toURI().toString());
    

    public static void placePiece(Circle circle, Color color){
        circle.setFill(color);
        sound.play();
    }

}
