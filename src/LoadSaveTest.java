/*
Skrevet af: Frederik Cayré Hede-Andersen
Studienummer: s224807
*/

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import org.junit.jupiter.api.MethodOrderer;

import java.util.ArrayList;
import java.io.File;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LoadSaveTest {

	@BeforeEach
	void localSetUp() throws Exception {
	}

	@Test
	@Order(1)
	void MakeFolderStructureTest() {
		assertTrue(FileHandler.CreateFolderStructure()); //Makes the folder structure if it isn't present already
		assertTrue(new File(FileHandler.getPath()).exists()); //Checks if the folder structure actually exists/got made

		assertTrue(FileHandler.CreateFolderStructure()); // Checks for errors when attempting to create the folder structure for a second time
	}

	@Test
	@Order(2)
	void SaveTest() {
		// Makes a SaveGame object
		ArrayList<Turn> t = new ArrayList<Turn>();
		int[] c = { 3, 2 };
		t.add(new Turn(c, Constants.PLACEMENT, 1));
		SaveGame saveGame = new SaveGame(t, new saveSettings());

		// Check if the saving of the file works. Only returns true if everything worked out
		assertTrue(FileHandler.SaveGame(saveGame));
		assertTrue(FileHandler.SaveGame(saveGame));
	}

	@Test
	@Order(3)
	// Tests if the loading of a save file works
	void LoadTest() {
		// Makes a SaveGame object
		saveSettings st = new saveSettings();
		ArrayList<Turn> t = new ArrayList<Turn>();
		int[] c = { 3, 2 };
		t.add(new Turn(c, Constants.PLACEMENT, 1));
		SaveGame saveGame = new SaveGame(t, st);

		// Saves the game
		FileHandler.SaveGame(saveGame);

		// Loads the game and checks if null
		// It would only be null if there had been an error
		SaveGame loadGame = FileHandler.LoadGame();
		assertNotEquals(null, loadGame);
	}

	// Old tests that doesn't work with the current version
	/*
	 * @Test
	 * 
	 * @Order(4) void ExportFileTest() { App app = new App(); String[] args = new String[2]; args[0] = "RunTests"; args[1] = "ExportTest";
	 * app.main(args); }
	 * 
	 * @Test
	 * 
	 * @Order(5)
	 * 
	 * @Disabled void ImportFileTest() { App app = new App(); String[] args = new String[2]; args[0] = "RunTests"; args[1] = "ImportTest";
	 * app.main(args); }
	 */
}
