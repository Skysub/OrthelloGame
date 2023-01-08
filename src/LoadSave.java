import java.io.File;
import java.util.ArrayList;
import java.io.FileWriter; // Import the FileWriter class
import java.io.IOException; // Import the IOException class to handle errors

public class LoadSave {
	public static String path = System.getenv("APPDATA") + "\\OthelloGame\\data";

	public static boolean SaveGame(ArrayList<String> moveList) {
		try {
			File newSave = new File(path + "\\save.txt");
			FileWriter myWriter = new FileWriter(newSave);
			for (int i = 0; i < moveList.size(); i++) {
				myWriter.write(moveList.get(i) + "\n");
			}
			myWriter.close();
		} catch (IOException e) {
			System.out.println("Exception: " + e);
			System.out.println("Error while saving the game");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean CreateFolderStructure() {
		// sørger for at de forskellige foldere og filer, som spillet skal bruge,
		// eksisterer
		try {
			// Makes data folder in appdata
			File directory = new File(path);
			if (!directory.exists()) {
				directory.mkdir();
			}

		} catch (Exception e) {
			System.out.println("Exception: " + e);
			System.out.println("Error creating folder Structure");
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
