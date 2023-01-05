import java.io.File;

public class LoadSave {
	public static String path = System.getenv("APPDATA")+"\\OthelloGame\\data";
	
	public static boolean SaveGame() {
		return true;
	}

	public static boolean CreateFolderStructure() {
		// sørger for at de forskellige foldere og filer, som spillet skal bruge, eksisterer
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
