/*
Skrevet af: Frederik Cayré Hede-Andersen
Studienummer: s224807
*/

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.FileOutputStream;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FileHandler {
	public static String path = System.getenv("APPDATA") + "\\OthelloGame\\data";

	public static boolean SaveGame(SaveGame save) {
		File saveFile = new File(path + "\\save.sav"); 	// Makes a File object with the default save path
		return SaveObjectToFile(save, saveFile); 		// Returns the satus message of the method that saves the file
	}

	public static SaveGame LoadGame() {
		File loadFile = new File(path + "\\save.sav"); 	// Makes a File object with the default save path
		if (!loadFile.exists()) { 						// Makes sure that the file actually exists before trying to load it
			System.out.println("No save file present");
			return null;
		}
		Object out = LoadObjectFromFile(loadFile); 		// Loads the file by passing the File object holding the identity of the file
		if (out instanceof SaveGame)					// Checks if the loaded file is of the correct type
			return (SaveGame) out;
		else {
			System.out.println("Loaded file is not of type SaveGame");
			return null;
		}
	}

	// Takes a file object that holds the location and a name for the file, as well as the Java object to save (SaveGame)
	public static boolean SaveObjectToFile(Object object, File saveFile) {
		try {
			FileOutputStream fileOut = new FileOutputStream(saveFile); 		// Makes the output stream to read to the file
			ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(object); 									// Write the object to the file
			objectOut.close();
		} catch (IOException e) {
			System.out.println("Exception: " + e);
			System.out.println("Error while saving the file");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// Takes a file object that holds the location and a name for the file
	public static Object LoadObjectFromFile(File loadFile) {
		try {
			FileInputStream fileIn = new FileInputStream(loadFile); 	// Makes the stream to be able to read the file
			ObjectInputStream objectIn = new ObjectInputStream(fileIn);
			Object object = objectIn.readObject(); 						// Reads the file
			objectIn.close();
			return object; 												// Return the loaded object
		} catch (Exception e) {
			System.out.println("Exception: " + e);
			System.out.println("Error while loading the file");
			e.printStackTrace();
			return null;
		}
	}
	
	// Takes the object to export (SaveGame), and the stage object to be able to open a new window
	public static boolean ExportFile(Object object, Stage primaryStage) {
		FileChooser fileChooser = new FileChooser();

		// We set the settings fileChooser window wer're about to open
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Save file (*.sav)", "*.sav"); // Filters for the savegame extension
		fileChooser.getExtensionFilters().add(extFilter);
		fileChooser.setTitle("Export Othello Save File");
		fileChooser.setInitialFileName("Othello Save");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "\\Desktop")); // A very reasonable place to the export the save file

		// We open the dialog window and the resulting File object is used later
		File exportFile = fileChooser.showSaveDialog(primaryStage);

		if (exportFile == null) // If the dialog window was closed or the cancel button was pressed
			return false;
		
		// We save the object at the location and with the name that the dialog gave us
		return SaveObjectToFile(object, exportFile);
	}
	
	// Needs the stage object to be able to open a new window
	public static Object ImportFile(Stage primaryStage) {
		FileChooser fileChooser = new FileChooser();

		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Save file (*.sav)", "*.sav"); // Filters for the savegame extension
		fileChooser.getExtensionFilters().add(extFilter);
		fileChooser.setTitle("Import Othello Save File");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "\\Desktop")); // The same place we default to when exporting

		// We open the dialog window and the resulting File object is used later
		File importFile = fileChooser.showOpenDialog(primaryStage);

		if (importFile == null) // If the dialog window was closed or the cancel button was pressed
			return null;

		// We load the object at the location and with the name that the dialog gave us
		return LoadObjectFromFile(importFile);
	}

	// We create the folder structure we need to store the game when saved, and for any possible future needs
	public static boolean CreateFolderStructure() {
		try {
			// Makes data folder in appdata
			File directory = new File(path);
			if (!directory.exists()) { 		// If the folder structure exists already we don't bother
				if (!directory.mkdirs()) 	// Returns true when successful
					System.out.println("Folder structure not created");
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e);
			System.out.println("Error creating folder Structure");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static String getPath() {
		return path;
	}
}

// A structure for holding onto all the information needed to load and save the game
class SaveGame implements Serializable{
	private static final long serialVersionUID = 4788495559812482875L; // Long required for serialization
	
	ArrayList<Turn> turns;
	saveSettings settings;

	public SaveGame(ArrayList<Turn> turns, saveSettings settings) {
		this.turns = turns;
		this.settings = settings;
	}

	public ArrayList<Turn> getTurns() {
		return turns;
	}

	public saveSettings getSettings() {
		return settings;
	}

	public void setTurns(ArrayList<Turn> t) {
		turns = t;
	}

	public void setSettings(saveSettings s) {
		settings = s;
	}
}
