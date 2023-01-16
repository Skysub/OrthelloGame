import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import javafx.scene.paint.Color;

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
		assertTrue(LoadSave.CreateFolderStructure());
		assertTrue(new File(LoadSave.getPath()).exists()); // tjekker at foleren den faktisk eksisterer

		assertTrue(LoadSave.CreateFolderStructure());
	}

	@Test
	@Order(2)
	void SaveTest() {
		SaveGame saveGame = new SaveGame(null, new saveSettings());
		assertTrue(LoadSave.SaveGame(saveGame)); // Tjekker om der bliver throwet exceptions
		assertTrue(LoadSave.SaveGame(saveGame));
	}

	@Test
	@Order(3)
	void LoadTest() {
		saveSettings st = new saveSettings();
		ArrayList<Turn> t = new ArrayList<Turn>();
		int[] c = { 3, 2 };
		t.add(new Turn(c, Constants.PLACEMENT, 1));
		SaveGame saveGame = new SaveGame(t, st);
		LoadSave.SaveGame(saveGame);

		SaveGame loadGame = LoadSave.LoadGame();
		assertTrue(loadGame.getSettings().playerColors.get(0).equals(Color.WHITE));
		assertNotEquals(null, loadGame);
	}
	/*
	 * @Test
	 * 
	 * @Order(4) void ExportFileTest() { App app = new App(); String[] args = new
	 * String[2]; args[0] = "RunTests"; args[1] = "ExportTest"; app.main(args); }
	 * 
	 * @Test
	 * 
	 * @Order(5)
	 * 
	 * @Disabled void ImportFileTest() { App app = new App(); String[] args = new
	 * String[2]; args[0] = "RunTests"; args[1] = "ImportTest"; app.main(args); }
	 */
}
