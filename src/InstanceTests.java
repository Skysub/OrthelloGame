import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;

public class InstanceTests {

	App app;

	public InstanceTests(App app) {
		this.app = app;
	}

	public void Run(List<String> arguments) {
		if (arguments.get(1).equals("ExportTest"))
			TestExportSave();
	}

	public void TestExportSave() {
		ArrayList<String> moveList = new ArrayList<String>();
		moveList.add("4");
		moveList.add("1 4-7");
		moveList.add("2 4-8");
		moveList.add("1 0");
		moveList.add("2 0");
		LoadSave.ExportReplayFile(moveList, app.getStage());

		// Testen er færdig og vi lukker programmet
		Platform.exit();
	}
}
