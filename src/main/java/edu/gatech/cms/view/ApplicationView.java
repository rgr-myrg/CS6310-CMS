package edu.gatech.cms.view;

import java.io.IOException;

import edu.gatech.cms.InputFileHandler;
import edu.gatech.cms.InputFileHandler.UiMode;
import edu.gatech.cms.controller.ScreenController;
import edu.gatech.cms.data.CoursesData;
import edu.gatech.cms.data.InstructorsData;
import edu.gatech.cms.data.PrerequisitesData;
import edu.gatech.cms.data.RecordsData;
import edu.gatech.cms.data.StudentsData;
import edu.gatech.cms.logger.Log;
import edu.gatech.cms.logger.Logger;
import edu.gatech.cms.util.DbHelper;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class ApplicationView {
	public static final String WELCOME_SCREEN = "/fxml/Welcome.fxml";
	public static final String APRIORI_SCREEN = "/fxml/Apriori.fxml";
	public static final String INSTRUCTOR_SCREEN = "/fxml/Instructor.fxml";
	public static final String PROCESSED_REQUESTS_SCREEN = "/fxml/ProcessedRequests.fxml";
	public static final String CONFIRM_EXIT_SCREEN = "/fxml/ConfirmExit.fxml";	

	public static final int SCENE_WIDTH  = 1000;
	public static final int SCENE_HEIGHT = 700;

	public Runnable onSemesterDataLoaded = null;
	public Runnable onAprioriResultsLoaded = null;
	public Runnable onInstructorResultsLoaded = null;
	public Runnable onProcessedRequestsResultsLoaded = null;
	public Runnable onConfirmExitResultsLoaded = null;

	private static ApplicationView instance = new ApplicationView();

	private UiMode uiMode = UiMode.INITIAL;
	private Stage stage = null;
	private String aprioriResults = null;
	private boolean isAppLaunch = false;

	public static final ApplicationView getInstance() {
		return instance;
	}

	public void onAppStart(Stage stage) {
		this.stage = stage;
		this.isAppLaunch = true;

		loadScreen(WELCOME_SCREEN, () -> {
			InputFileHandler.load();

			Platform.runLater(() -> {
				stage.setTitle(UiMessages.WELCOME_WINDOW_TITLE);

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

	public UiMode getUiMode() {
		return uiMode;
	}

	public void loadScreen(final String fxmlPath, final Runnable target) {
		if (stage != null) {
			stage.close();
		}

		try {
			final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
			final Parent screen = fxmlLoader.load();
			final Scene scene = new Scene(screen, SCENE_WIDTH, SCENE_HEIGHT);
			final ScreenController controller = (ScreenController) fxmlLoader.getController();

			if (!isAppLaunch) {
				stage = controller.getStage();
			}

			stage.setTitle(UiMessages.LOADING_WINDOW_TITLE);
			stage.setScene(scene);
			stage.setResizable(true);

			stage.show();

			new Thread(target).start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void displayAppInfoAlertDialog() {
		Alert alert = new Alert(AlertType.INFORMATION);

		alert.setTitle(UiMessages.ABOUT_WINDOW_TITLE);
		alert.setHeaderText(UiMessages.APP_TITLE);
		alert.setContentText(UiMessages.APP_INFO);
		alert.setResizable(false);

		alert.show();
	}

	public void onWelcomeControllerNextAction(UiMode uiMode) {
		this.uiMode = uiMode;

		loadScreen(APRIORI_SCREEN, () -> {
			
			// if possible it was already loaded from DB, 
			// in which case an "initial" choice should reset the data
			if (uiMode == UiMode.INITIAL) {
				DbHelper.dropTables();
				DbHelper.createTables();

				//load from CSV
				StudentsData.loadFromCSV();
				CoursesData.loadFromCSV();
				PrerequisitesData.loadFromCSV();
				InstructorsData.loadFromCSV();
				RecordsData.loadFromCSV();
				
				InputFileHandler.setCurrentSemester(0);
			}
			
			InputFileHandler.initialSemester();
			
			if (Log.isDebug()) Logger.debug(APRIORI_SCREEN, "Current semester (welcome): " + InputFileHandler.getCurrentSemester());

			InputFileHandler.loadAssignments();
			InputFileHandler.loadRequests();
			aprioriResults = InputFileHandler.runDataMining();

			Platform.runLater(() -> {
				stage.setTitle(UiMessages.APRIORI_WINDOW_TITLE);

				if (onAprioriResultsLoaded != null) {
					onAprioriResultsLoaded.run();
				}
			});
		});
	}

	public void onAprioriControllerNextAction() {
		loadScreen(INSTRUCTOR_SCREEN, () -> {
			Platform.runLater(() -> {
				stage.setTitle(UiMessages.INSTRUCTOR_WINDOW_TITLE);
				
				if (onInstructorResultsLoaded != null) {
					onInstructorResultsLoaded.run();
				}
			});
		});		
	}

	public void onInstructorControllerNextAction(){
		loadScreen(PROCESSED_REQUESTS_SCREEN, () -> {
			Platform.runLater(() -> {
				stage.setTitle(UiMessages.PROCESSED_REQUESTS_WINDOW_TITLE);
				
				if (onProcessedRequestsResultsLoaded != null) {
					onProcessedRequestsResultsLoaded.run();
				}
			});
		});		
	}

	public void onProcessedRequestsControllerNextSemesterAction(){
		loadScreen(APRIORI_SCREEN, () -> {
			InputFileHandler.nextSemester();
			
			if (Log.isDebug()) Logger.debug(APRIORI_SCREEN, "Current semester (next): " + InputFileHandler.getCurrentSemester());
			
			InputFileHandler.loadAssignments();
			InputFileHandler.loadRequests();
			aprioriResults = InputFileHandler.runDataMining();

			Platform.runLater(() -> {
				stage.setTitle(UiMessages.APRIORI_WINDOW_TITLE);

				if (onAprioriResultsLoaded != null) {
					onAprioriResultsLoaded.run();
				}
			});
		});
	}

	public void exitAction(){
	    Stage stage = getStage();	    
	    stage.close();
	}

}
