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


public class ConfirmExitController implements ScreenController{
	@FXML private Group progressGroup;
	@FXML private ImageView progressGif;
	@FXML private Text welcomeText;
	@FXML private TextArea confirmOutputMessage;

	public static final String TAG = InputFileHandler.class.getSimpleName();

	public ConfirmExitController(){
		ApplicationView.getInstance().onConfirmExitResultsLoaded = () -> {
			progressGroup.getChildren().remove(progressGif);

			final String screenMsg = String.format(UiMessages.CONFIRM_EXIT_HEADING);
	        welcomeText.setText(screenMsg);
	        confirmOutputMessage.setText(UiMessages.CONFIRM_EXIT_BODY);
		};
	}

	@FXML protected void onAboutMenuSelected(ActionEvent event) {
		ApplicationView.getInstance().displayAppInfoAlertDialog();
	}

	@FXML protected void onBackButtonClick(ActionEvent event) {
		ApplicationView.getInstance().onConfirmExitControllerBackAction();
	}

	@FXML protected void onExitButtonClick(ActionEvent event) {
		ApplicationView.getInstance().onConfirmExitControllerExitAction();
	}

	@Override
	public Stage getStage() {
		return (Stage) progressGroup.getScene().getWindow();
	}
}