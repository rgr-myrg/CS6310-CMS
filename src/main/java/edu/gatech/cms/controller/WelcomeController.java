package edu.gatech.cms.controller;

import edu.gatech.cms.InputFileHandler;
import edu.gatech.cms.InputFileHandler.UiMode;
import edu.gatech.cms.view.ApplicationView;
import edu.gatech.cms.view.UiMessages;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class WelcomeController implements ScreenController {
	public static final String TAG = WelcomeController.class.getSimpleName();

	@FXML private Group progressGroup;
	@FXML private Group resumeGroup;

	@FXML private ToggleGroup radioToggleGroup;
	@FXML private ImageView progressGif;

	@FXML private Text welcomeText;
	@FXML private Text welcomeInfoText;
	@FXML private Text textMessageBox;

	public WelcomeController() {
		ApplicationView.getInstance().onSemesterDataLoaded = () -> {
			progressGroup.getChildren().remove(progressGif);

			final int semesterCount = InputFileHandler.getCurrentSemester();
			String welcomeInfoMsg = UiMessages.START_SEMESTER;

			if(semesterCount == 0 && InputFileHandler.finishedAllSemesters()){
				welcomeInfoMsg = String.format(UiMessages.START_ONLY);
				resumeGroup.setVisible(false);
			}
			else if(semesterCount >= 1) {
				welcomeInfoMsg = String.format(UiMessages.RESUME_SEMESTER, semesterCount);
				resumeGroup.setVisible(true);
			}


			welcomeInfoText.setText(welcomeInfoMsg);
			welcomeText.setText(UiMessages.WELCOME_HEADING);
		};
	}

	@FXML protected void onNextButtonClick(ActionEvent event) {
		final RadioButton selectedRadioButton = (RadioButton) radioToggleGroup.getSelectedToggle();

		ApplicationView.getInstance().onWelcomeControllerNextAction(
				selectedRadioButton.getId().equals("resumeOption") 
					? UiMode.RESUME 
						: UiMode.INITIAL
				);
	}

	@FXML protected void onAboutMenuSelected(ActionEvent event) {
		ApplicationView.getInstance().displayAppInfoAlertDialog();
	}

	@Override
	public Stage getStage() {
		return (Stage) progressGroup.getScene().getWindow();
	}
}
