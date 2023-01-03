import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class SettingsView extends ViewState {

    public static int boardSize = 8;

    private RadioButton set8 = new RadioButton();
    private RadioButton set16 = new RadioButton();
    private RadioButton setCustom = new RadioButton();
    private TextField customSize = new TextField(); //Interactable text box for custom sizes
    private Text title = new Text();
    
    public SettingsView(Model model, App view, Stage stage) {
		super(model, view, stage);

        //sets up the root
        Pane root = new Pane();
        VBox forTitle = new VBox();
        forTitle.getChildren().add(title);
        root.getChildren().add(forTitle);
        scene = new Scene(root,800,800,Color.LIGHTGREY);
        root.setBackground(null);
        //sets up chechmarkers
        Pane sizeBox = gameSizeBox(scene.getWidth()/17*3,175,35,Color.GREY);
        root.getChildren().add(sizeBox);
        sizeBox.setLayoutX(scene.getWidth()/17*2);
        sizeBox.setLayoutY(scene.getHeight()/10*3);
        
        //sets up text
        title.setText("Settings");
        title.setFont(new Font(150));
        forTitle.setMinWidth(800);
        forTitle.setAlignment(Pos.CENTER);
    }

	@Override
	public void OnEnter() {
		stage.setScene(scene);		
	}
    
    private Pane gameSizeBox(double width,  double height, double gap, Color color){
        Rectangle background = new Rectangle(width,height,color); //The bacground is a rectangle
        //Makes text for our box with text notes
        Text text = new Text("Set Board size"); //Titel of box
        
        text.setFont(new Font(20));
        VBox forText = new VBox();
        forText.getChildren().add(text);
        forText.getChildren().add(customSize);
        forText.setSpacing(100);
        customSize.setMaxWidth(width-30);
        forText.setAlignment(Pos.CENTER);
        forText.setMinWidth(width);

        //Makes the "return" pane and adds notes
        Pane checkers = new Pane();
        checkers.setMinWidth(width);
        checkers.getChildren().add(background);
        checkers.getChildren().add(forText);
        checkers.getChildren().add(set8);
        checkers.getChildren().add(set16);
        checkers.getChildren().add(setCustom);
        
        //TODO: Omskriv følgende del. Man skal ikke accesse andre views på den her måde.
        //Lav f.eks. en klasse der nedarver fra button og tilføj metoden buttonPos
        MenuView.buttonPos(set8, width/15, height/6);
        MenuView.buttonPos(set16, width/15,height/6+gap);
        MenuView.buttonPos(setCustom, width/15,height/6+gap*2);
        
        //adds text to boxes and sets actions
        ToggleGroup group = new ToggleGroup();
        set8.setToggleGroup(group);
        set16.setToggleGroup(group);
        setCustom.setToggleGroup(group);
        set8.setText("8 x 8 (default)");
        set16.setText("16 x 16");
        setCustom.setText("Custom Size");
        set8.setOnAction(this::action8);
        set16.setOnAction(this::action16);
        setCustom.setOnAction(this::actionCust);
        return checkers;
    }

    private void action8(ActionEvent event){ //Action for checkmark 8
        boardSize = 8;
    }

    private void action16(ActionEvent event){ //Action for checkmark 16
        boardSize = 16;
    }

    private void actionCust(ActionEvent event){ //Action for checkmark Custom
        for(char c : customSize.getText().toCharArray()){
            if(!(48 <= c && c <=57)){
                customSize.setText("Must be integer");
            }
        }
        boardSize = Integer.valueOf(customSize.getText());
        System.out.println(boardSize);
    }

    @Override
	public void Reset() {
				
	}    
}
