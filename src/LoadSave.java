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
		try {
			File saveFile = new File(path + "\\save.txt");
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

	public static ArrayList<String> LoadGame() {
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

	public static boolean CreateFolderStructure() {
		// sørger for at de forskellige foldere og filer, som spillet skal bruge,
		// eksisterer
		try {
			// Makes data folder in appdata
			File directory = new File(path);
			if (!directory.exists()) {
				if (!directory.mkdirs())
					System.out.println("Folder structure not created");
				;
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
		 
        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File exportFile = fileChooser.showSaveDialog(primaryStage);

        if (exportFile == null) return false;
        
		try {
			FileWriter myWriter = new FileWriter(exportFile);
			for (int i = 0; i < moveList.size(); i++) {
				myWriter.write(moveList.get(i) + "\n");
			}
			myWriter.close();
		} catch (IOException e) {
			System.out.println("Exception: " + e);
			System.out.println("Error while exporting the moveList");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static String getPath() {
		return path;
	}
}
