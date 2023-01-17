import java.util.ArrayList;
import javafx.animation.FadeTransition;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class SaveLoad {
	GameView view;
	GameController con;
	private Label saveLoadText;
	
	public SaveLoad(GameView view) {
		this.view = view;
	}
	
	public void setController(GameController controller) {
		this.con = controller;
	}
	
	public void setText(Label saveLoadText) {
		this.saveLoadText = saveLoadText;
	}
	
	public void SaveGame(boolean export) {
		// Constructs a single arraylist of all turns taken, in order, from the Players' turnHistory
		ArrayList<Turn> turns = new ArrayList<Turn>();
		int first = con.model.gamePlayerManager.getFirstPlayerIndex(); // takes into account the starting player
		int playerTurnIndex = -1; //Used to keep track of what turnindex to use with the individual turnHistories
		int extra = 0;
		
		if (con.model.turnsTaken < 4) {
			saveLoadText.setText("Can't save game under 4 turns");
			FadeTransition fader = createFader(saveLoadText); //Fading error message
			saveLoadText.setVisible(true);
			fader.play();
			return;
		}
		if(Settings.gameMode == Constants.GAMEMODE_REVERSI) {
			//adds the first 4 startingmoves
			if (con.model.nrPlayers == 2) {
				turns.add(con.model.gamePlayerManager.players.get(first % con.model.nrPlayers).getTurnHistory().get(0));
				turns.add(con.model.gamePlayerManager.players.get(first % con.model.nrPlayers).getTurnHistory().get(1));
				turns.add(con.model.gamePlayerManager.players.get((first + 1) % con.model.nrPlayers).getTurnHistory().get(0));
				turns.add(con.model.gamePlayerManager.players.get((first + 1) % con.model.nrPlayers).getTurnHistory().get(1));
				playerTurnIndex++;
			} else {
				turns.add(con.model.gamePlayerManager.players.get(first % con.model.nrPlayers).getTurnHistory().get(0));
				turns.add(con.model.gamePlayerManager.players.get((first + 1) % con.model.nrPlayers).getTurnHistory().get(0));
				turns.add(con.model.gamePlayerManager.players.get((first + 2) % con.model.nrPlayers).getTurnHistory().get(0));
				turns.add(con.model.gamePlayerManager.players.get((first + 3) % con.model.nrPlayers).getTurnHistory().get(0));
			}
			extra = 4;
			playerTurnIndex++;
		}
		
		//Loops through once for each turn. Used i and the playerTurnIndex to get the correct turn of the correct player
		for (int i = first + extra; i < con.model.turnsTaken + first; i++) {
			if ((i - first) % con.model.nrPlayers == 0) {
				// increments playerTurnIndex when all players' turn of the current index has been recorded
				playerTurnIndex++;
			}
			//adds the correct turn to the single list
			turns.add(con.model.gamePlayerManager.players.get(i % con.model.nrPlayers).getTurnHistory().get(playerTurnIndex));
		}
		SaveGame save = new SaveGame(turns, new saveSettings()); //Makes a SaveGame object from the collected data

		
		if (!export)
			FileHandler.SaveGame(save); // Calls the method that saves the file
		else {
			if (!FileHandler.ExportFile(save, view.manager.stage)) { // Calls the method that actually saves the file, returns true if all goes well
				saveLoadText.setText("Game not saved");
				FadeTransition fader = createFader(saveLoadText); //Fading error message
				saveLoadText.setVisible(true);
				fader.play();
				return; 
			}
		}
		saveLoadText.setText("Game Successfully saved");
		FadeTransition fader = createFader(saveLoadText); //Message conveying the success of the saving
		saveLoadText.setVisible(true);
		fader.play();
	}

	//Loads the game from a SaveGame object
	public void LoadGame(SaveGame save) {
		if(Animation.isAnimating() || con.model.currentPlayer.isAI()) {
			saveLoadText.setText("Can't load the game while animation is playing or AI's turn");
			FadeTransition fader = createFader(saveLoadText);
			saveLoadText.setVisible(true);
			fader.play();
			return;
		}
		if (save == null) { //error message and returns if theres no save
			saveLoadText.setText("Error while loading the game");
			FadeTransition fader = createFader(saveLoadText);
			saveLoadText.setVisible(true);
			fader.play();
			return;
		}
		Settings.setSettings(save.getSettings()); // Makes Settings mirror the settings of the saved game
		ArrayList<Turn> turns = save.getTurns();

		view.LoadInitialization(); // Makes a new model to recreate the game from a fresh board with the correct new settings
		con.model.selectStartingPlayer(turns.get(0).playerIndex); //Tells the model of the correct startingplayer

		if(Settings.gameMode == Constants.GAMEMODE_OTHELLO) {
			var m = (OthelloModel) con.model;
			m.startingMoves();
		}
		
		for (int i = 0; i < turns.size(); i++) {
			try {
				con.model.step(turns.get(i).coordinates); // Plays the board up to the latest move from the turn list
			} catch (Exception e) {
				System.out.println("Exception: " + e);
				System.out.println("Error while replaying the game");
				e.printStackTrace();

				saveLoadText.setText("Error while loading, save file might be corrupted");
				FadeTransition fader = createFader(saveLoadText); //fading error message
				saveLoadText.setVisible(true);
				fader.play();

				// Makes fresh board
				view.LoadInitialization();
				con.model.selectStartingPlayer(turns.get(0).playerIndex);
				break;
			}
		}
		//Makes a visual bug dissappear
		view.initializeBoard();
		view.updateBoard(con.model.gameBoard);
		view.updateTurnText(con.model.currentPlayer);
		con.getGameEndScreen().setVisible(false);

		//If it's the AI's turn this makes it move
		if (con.model.currentPlayer.isAI()) {
			con.AIPress();
		}
	}
	//Makes the animation of the error text
	FadeTransition createFader(Label label) {
		FadeTransition fade = new FadeTransition(Duration.seconds(5), label);
		fade.setFromValue(1);
		fade.setToValue(0);
		return fade;
	}
}
