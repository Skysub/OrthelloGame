import java.io.File;
import java.util.ArrayList;
import java.io.FileWriter; // Import the FileWriter class
import java.io.IOException; // Import the IOException class to handle errors
import java.io.FileNotFoundException; // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class LoadSave {
	public static String path = System.getenv("APPDATA") + "\\OthelloGame\\data";

	public static boolean SaveGame(ArrayList<String> moveList) {
		File saveFile = new File(path + "\\save.txt");
		return SaveToFile(moveList, saveFile);
	}

	public static ArrayList<String> LoadGame() {
		File loadFile = new File(path + "\\save.txt");
		return LoadFromFile(loadFile);
	}

	public static boolean CreateFolderStructure() {
		// sørger for at de forskellige foldere og filer, som spillet skal bruge,
		// eksisterer
		try {
			// Makes data folder in appdata
			File directory = new File(path);
			if (!directory.exists()) {
				if (!directory.mkdirs())
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

	public static boolean ExportReplayFile(ArrayList<String> moveList, Stage primaryStage) {
		FileChooser fileChooser = new FileChooser();

		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
		fileChooser.getExtensionFilters().add(extFilter);
		fileChooser.setTitle("Export Othello Replay File");
		fileChooser.setInitialFileName("Othello Replay");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "\\Desktop"));

		File exportFile = fileChooser.showSaveDialog(primaryStage);

		if (exportFile == null)
			return false;

		return SaveToFile(moveList, exportFile);
	}

	public static ArrayList<String> ImportReplayFile(Stage primaryStage) {
		FileChooser fileChooser = new FileChooser();

		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
		fileChooser.getExtensionFilters().add(extFilter);
		fileChooser.setTitle("Import Othello Replay File");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "\\Desktop"));

		File importFile = fileChooser.showOpenDialog(primaryStage);

		if (importFile == null)
			return null;

		return LoadFromFile(importFile);
	}

	public static boolean SaveToFile(ArrayList<String> moveList, File saveFile) {
		try {
			FileWriter myWriter = new FileWriter(saveFile);
			for (int i = 0; i < moveList.size(); i++) {
				myWriter.write(moveList.get(i) + "\n");
			}
			myWriter.close();
		} catch (IOException e) {
			System.out.println("Exception: " + e);
			System.out.println("Error while saving the moveList");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static ArrayList<String> LoadFromFile(File loadFile) {
		ArrayList<String> moveList = new ArrayList<String>();
		try {
			File saveFile = new File(path + "\\save.txt");
			Scanner myReader = new Scanner(saveFile);
			while (myReader.hasNextLine()) {
				moveList.add(myReader.nextLine());
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("Exception: " + e);
			System.out.println("Error while loading the moveList");
			e.printStackTrace();
			return null;
		}
		return moveList;
	}

	public static String getPath() {
		return path;
	}
}
