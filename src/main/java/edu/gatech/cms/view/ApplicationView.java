package edu.gatech.cms.view;

import java.io.IOException;

import edu.gatech.cms.InputFileHandler;
import edu.gatech.cms.controller.ScreenController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class ApplicationView {
	public static final String WELCOME_SCREEN = "/fxml/Welcome.fxml";
	public static final String APRIORI_SCREEN = "/fxml/Apriori.fxml";

	public static final int SCENE_WIDTH  = 600;
	public static final int SCENE_HEIGHT = 400;

	public static enum ScreenAction {
			START,
			RESUME
	};

	public Runnable onSemesterDataLoaded = null;
	public Runnable onAprioriResultsLoaded = null;

	private static ApplicationView instance = new ApplicationView();
//	private ScreenAction currentScreenAction = ScreenAction.START;

	private Stage stage = null;
	private String aprioriResults = null;

	public static final ApplicationView getInstance() {
		return instance;
	}

	public void onAppStart(Stage stage) {
		this.stage = stage;

		loadScreen(WELCOME_SCREEN, () -> {
			InputFileHandler.loadFromCSV();
			InputFileHandler.designateSemester();

			Platform.runLater(() -> {
				stage.setTitle("CMS :: Welcome");

				if (onSemesterDataLoaded != null) {
					onSemesterDataLoaded.run();
				}
			});
		});
	}

	public Stage getStage() {
		return stage;
	}

	public String getAprioriResults() {
		return aprioriResults;
	}

	public void loadScreen(final String fxmlPath, final Runnable target) {
		if (stage != null) {
			stage.close();
		}

		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
			//final Parent screen = FXMLLoader.load(getClass().getResource(fxmlPath));
			final Parent screen = fxmlLoader.load();
			final Scene scene = new Scene(screen, SCENE_WIDTH, SCENE_HEIGHT);

			scene.setCursor(Cursor.WAIT);

			final ScreenController controller = (ScreenController) fxmlLoader.getController();

			if (stage == null) {
				stage = controller.getStage();
			}

			stage.setTitle("CMS :: Loading");
			stage.setScene(scene);
			stage.setResizable(true);
			//stage.setFullScreen(true);

			new Thread(target).start();

			new Thread(() -> {
				Platform.runLater(() -> {
					scene.setCursor(Cursor.DEFAULT);
				});
			}).start();

			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void displayAppInfoAlertDialog() {
		Alert alert = new Alert(AlertType.INFORMATION);

		alert.setTitle("CMS :: About");
		alert.setHeaderText(UiMessages.APP_TITLE);
		alert.setContentText(UiMessages.APP_INFO);
		alert.setResizable(false);

		alert.showAndWait();
	}

	public void onWelcomeControllerNextAction(ScreenAction screenAction) {
		//this.currentScreenAction = screenAction;
		loadScreen(APRIORI_SCREEN, () -> {
			InputFileHandler.prepareDataForDataMining();
			aprioriResults = InputFileHandler.analyzeHistoryAndRoster();

			Platform.runLater(() -> {
				stage.setTitle("CMS :: Apriori Analysis");

				if (onAprioriResultsLoaded != null) {
					onAprioriResultsLoaded.run();
				}
			});
		});
	}
}
