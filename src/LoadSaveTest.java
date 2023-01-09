import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import java.util.ArrayList;
import java.io.File;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LoadSaveTest {

	static ArrayList<String> moveList;

	@BeforeEach
	void setUp() throws Exception {
		moveList = new ArrayList<String>();
		moveList.add("4");
		moveList.add("1 4-7");
		moveList.add("2 4-8");
		moveList.add("1 0");
		moveList.add("2 0");
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
		assertTrue(LoadSave.SaveGame(moveList)); // Tjekker om der bliver throwet exceptions
		assertTrue(LoadSave.SaveGame(moveList));
	}

	@Test
	@Order(3)
	void LoadTest() {
		LoadSave.SaveGame(moveList);
		ArrayList<String> loadList = LoadSave.LoadGame();
		assertNotEquals(null, loadList);

		// Sammenligner hver string entry i ArrayListen
		for (int i = 0; i < loadList.size(); i++) {
			assertEquals(moveList.get(i), loadList.get(i));
		}
	}

	@Test
	@Order(4)
	void ExportFileTest() {
		App app = new App();
		String[] args = new String[2];
		args[0] = "RunTests";
		args[1] = "ExportTest";
		app.main(args);
	}
}
