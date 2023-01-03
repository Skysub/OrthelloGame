import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class SettingPage extends Application {

    private CheckBox by8 = new CheckBox("8 x 8");
    private CheckBox by16 = new CheckBox("16 x 16");
    private Text title = new Text();
    public void start(Stage stage) throws Exception {
        //sets up the root
        Pane root = new Pane();
        VBox forTitle = new VBox();
        root.getChildren().add(by8);
        root.getChildren().add(by16);
        forTitle.getChildren().add(title);
        root.getChildren().add(forTitle);
        Scene scene = new Scene(root,800,800,Color.BEIGE);
        
        //sets up text
        title.setText("Settings");
        Font font = new Font(1);
        title.setFont(font.font(150));
        forTitle.setMinWidth(800);
        forTitle.setAlignment(Pos.CENTER);

        stage.setTitle("Settings Screen");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
