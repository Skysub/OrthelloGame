import java.io.File;
import java.util.ArrayList;
import java.io.FileWriter; // Import the FileWriter class
import java.io.IOException; // Import the IOException class to handle errors
import java.io.FileNotFoundException; // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

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
