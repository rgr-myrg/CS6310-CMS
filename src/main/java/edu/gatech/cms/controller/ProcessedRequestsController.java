package edu.gatech.cms.controller;

import edu.gatech.cms.InputFileHandler;
import edu.gatech.cms.view.ApplicationView;
import edu.gatech.cms.view.UiMessages;
import edu.gatech.cms.logger.Log;
import edu.gatech.cms.logger.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class ProcessedRequestsController implements ScreenController{
	@FXML private Group progressGroup;
	@FXML private ImageView progressGif;
	@FXML private Text welcomeText;

	public static final String TAG = InputFileHandler.class.getSimpleName();

	public ProcessedRequestsController(){
		ApplicationView.getInstance().onProcessedRequestsResultsLoaded = () -> {
			progressGroup.getChildren().remove(progressGif);

			final String screenMsg = String.format(UiMessages.PROCESSED_REQUESTS_HEADING);
	        welcomeText.setText(screenMsg);
		};
	}
	
	@FXML protected void onAboutMenuSelected(ActionEvent event) {
		ApplicationView.getInstance().displayAppInfoAlertDialog();
	}

	@FXML protected void onNextSemesterButtonClick(ActionEvent event) {
		ApplicationView.getInstance().onProcessedRequestsControllerNextSemesterAction();
	}

	@FXML protected void onExitButtonClick(ActionEvent event) {
		ApplicationView.getInstance().onProcessedRequestsControllerExitAction();
	}

	@Override
	public Stage getStage() {
		return (Stage) progressGroup.getScene().getWindow();
	}
}