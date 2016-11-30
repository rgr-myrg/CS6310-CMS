package edu.gatech.cms.controller;

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

public class AprioriController implements ScreenController {
	@FXML private Group progressGroup;
	@FXML private ImageView progressGif;
	@FXML private Text welcomeText;
	@FXML private TextArea aprioriTextResults;

	public AprioriController() {
		ApplicationView.getInstance().onAprioriResultsLoaded = () -> {
			progressGroup.getChildren().remove(progressGif);

			final int semesterCount = InputFileHandler.getCurrentSemester();
			final String screenMsg = String.format(UiMessages.APRIORI_HEADING, semesterCount);

			welcomeText.setText(screenMsg);
			aprioriTextResults.setText( ApplicationView.getInstance().getAprioriResults());
		};
	}

	@FXML protected void onAboutMenuSelected(ActionEvent event) {
		ApplicationView.getInstance().displayAppInfoAlertDialog();
	}

	@FXML protected void onNextButtonClick(ActionEvent event) {
		ApplicationView.getInstance().onAprioriControllerNextAction();
	}

	@Override
	public Stage getStage() {
		return (Stage) progressGroup.getScene().getWindow();
	}
}
