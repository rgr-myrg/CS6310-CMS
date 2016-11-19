package edu.gatech.cms;

import java.io.IOException;

import edu.gatech.cms.data.AssignmentsData;
import edu.gatech.cms.data.CoursesData;
import edu.gatech.cms.data.InstructorsData;
import edu.gatech.cms.data.PrerequisitesData;
import edu.gatech.cms.data.RecordsData;
import edu.gatech.cms.data.RequestsData;
import edu.gatech.cms.data.StudentsData;
import edu.gatech.cms.data.WekaDataSource;
import edu.gatech.cms.logger.Log;
import edu.gatech.cms.util.DbHelper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	public static final String WELCOME_SCREEN = "/fxml/Welcome.fxml";
	public static final int SCENE_WIDTH  = 500;
	public static final int SCENE_HEIGHT = 300;

	@Override
	public void start(Stage stage) throws IOException {
		final Parent parent = FXMLLoader.load(getClass().getResource(WELCOME_SCREEN));
		final Scene scene = new Scene(parent, SCENE_WIDTH, SCENE_HEIGHT);

		stage.setTitle("Team Fantastic 4 Welcome Screen");
		stage.setScene(scene);

//		stage.setMaximized(false);
//		stage.setMinHeight(500.0);
//		stage.setMinWidth(850.0);

		stage.show();
	}

	public static void main(String[] args) {
		if (args != null && args.length > 0 && args[0] != null) {
			final String option = args[0];
			Log.setDebug(option.equalsIgnoreCase("debug"));
		}

		loadFromCSV();
		launch(args);
	}

	public static final void loadFromCSV() {
		System.out.println("LOADING!!!");
		new Thread(
				new Runnable() {
					@Override
					public void run() {
						DbHelper.dropTables();
						DbHelper.createTables();

						CoursesData.load();
						InstructorsData.load();
						RecordsData.load();
						StudentsData.load();
						PrerequisitesData.load();

						// Load requests and assignments for each semester using the cycle number
						// Ex: 
						RequestsData.load(1);
						AssignmentsData.load(1);

						try {
							final WekaDataSource wekaDataSource = new WekaDataSource();
							System.out.println(wekaDataSource.analyzeStudentRecords());
							System.out.println(wekaDataSource.analyzeStudentRequests());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
		).start();
	}
}
