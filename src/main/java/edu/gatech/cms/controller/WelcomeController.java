package edu.gatech.cms.controller;

import edu.gatech.cms.InputFileHandler;
import edu.gatech.cms.view.ApplicationView;
import edu.gatech.cms.view.UiMessages;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class WelcomeController {
	public static final String TAG = WelcomeController.class.getSimpleName();

	@FXML private Group progressGroup;
	@FXML private Group resumeGroup;

	@FXML private ToggleGroup radioToggleGroup;
	@FXML private ImageView progressGif;

	@FXML private Text welcomeText;
	@FXML private Text textMessageBox;

	public WelcomeController() {
		ApplicationView.getInstance().onSemesterDataLoaded = () -> {
			progressGroup.getChildren().remove(progressGif);

			final int semesterCount = InputFileHandler.getInstance().getCurrentSemester();
			String welcomeMsg = UiMessages.START_SEMESTER;

			if (semesterCount > 1) {
				welcomeMsg = String.format(UiMessages.RESUME_SEMESTER, semesterCount);
				resumeGroup.setVisible(true);
			}

			welcomeText.setText(welcomeMsg);
		};
	}

	@FXML protected void onNextButtonClick(ActionEvent event) {
		final RadioButton selectedRadioButton = (RadioButton) radioToggleGroup.getSelectedToggle();

		switch (selectedRadioButton.getId()) {
			case "resumeOption":
				break;

			case "startOption":
				break;

			case "exitOption":
				Platform.exit();
				break;
		}

		textMessageBox.setText("Controller says: clicked! " + selectedRadioButton.getId());
	}

	@FXML protected void onAboutMenuSelected(ActionEvent event) {
		ApplicationView.getInstance().displayAppInfoAlertDialog();
	}
//
//	@FXML protected void onExitButtonClick(ActionEvent event) {
//		Platform.exit();
//	}
}
