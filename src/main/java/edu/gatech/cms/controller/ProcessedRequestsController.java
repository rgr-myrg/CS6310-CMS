package edu.gatech.cms.controller;

import java.util.Optional;

import edu.gatech.cms.InputFileHandler;
import edu.gatech.cms.view.ApplicationView;
import edu.gatech.cms.view.UiMessages;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Button;

public class ProcessedRequestsController implements ScreenController{
	@FXML private Group progressGroup;
	@FXML private ImageView progressGif;
	@FXML private Text welcomeText;
	@FXML private TextArea statsTextResults;
	@FXML private Button nextButton1;

	public static final String TAG = InputFileHandler.class.getSimpleName();

	public ProcessedRequestsController(){
		ApplicationView.getInstance().onProcessedRequestsResultsLoaded = () -> {
			progressGroup.getChildren().remove(progressGif);

			final String screenMsg = String.format(UiMessages.PROCESSED_REQUESTS_HEADING, InputFileHandler.getCurrentSemester());
	        welcomeText.setText(screenMsg);
	        
	        statsTextResults.setText(
	        		InputFileHandler.getProcessedRequests() + "\n"
	        		+ InputFileHandler.getSemesterStats() + "\n" 
	        		+ InputFileHandler.getAcademicRecords() + "\n" 
	        		+ InputFileHandler.getNewWaitingRequests());

	        if (! InputFileHandler.moreSemesters()) {
		    	nextButton1.setText("No more semesters");
		    	nextButton1.setDisable(true);
	        }
		};
	}
	
	@FXML protected void onAboutMenuSelected(ActionEvent event) {
		ApplicationView.getInstance().displayAppInfoAlertDialog();
	}

	@FXML protected void onNextSemesterButtonClick(ActionEvent event) {
		ApplicationView.getInstance().onProcessedRequestsControllerNextSemesterAction();
	}

	@FXML protected void onExitButtonClick(ActionEvent event) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(UiMessages.CONFIRM_EXIT_TITLE);
		alert.setHeaderText(UiMessages.CONFIRM_EXIT_HEADING);
		alert.setResizable(true);
		alert.getDialogPane().setPrefSize(300, 200);
		alert.setContentText(UiMessages.CONFIRM_EXIT_BODY);
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			ApplicationView.getInstance().exitAction();
		}
	}		

	@Override
	public Stage getStage() {
		return (Stage) progressGroup.getScene().getWindow();
	}
}