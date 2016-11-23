package edu.gatech.cms.view;

import java.io.IOException;

import edu.gatech.cms.InputFileHandler;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ApplicationView {
	public static final String WELCOME_SCREEN = "/fxml/Welcome.fxml";
	public static final int SCENE_WIDTH  = 600;
	public static final int SCENE_HEIGHT = 400;

	public Runnable onSemesterDataLoaded = null;

	private static ApplicationView instance = new ApplicationView();
	private Stage stage = null;

	public void onAppStart(Stage stage) {
		this.stage = stage;

		try {
			final Parent welcomeScreen = FXMLLoader.load(getClass().getResource(WELCOME_SCREEN));
			final Scene welcomeScene = new Scene(welcomeScreen, SCENE_WIDTH, SCENE_HEIGHT);

			welcomeScene.setCursor(Cursor.WAIT);

			stage.setTitle("CMS :: Loading");
			stage.setScene(welcomeScene);
			stage.setResizable(false);

			new Thread(() -> {
				InputFileHandler.getInstance().loadFromCSV();
				InputFileHandler.getInstance().designateSemester();

				Platform.runLater(() -> {
					stage.setTitle("CMS :: Welcome");

					if (onSemesterDataLoaded != null) {
						onSemesterDataLoaded.run();
					}

					welcomeScene.setCursor(Cursor.DEFAULT);
				});
			}).start();

			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Stage getStage() {
		return stage;
	}

	public static final ApplicationView getInstance() {
		return instance;
	}
}
