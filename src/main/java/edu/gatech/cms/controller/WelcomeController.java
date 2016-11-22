package edu.gatech.cms.controller;

import edu.gatech.cms.view.ApplicationView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class WelcomeController {
	public static final String TAG = WelcomeController.class.getSimpleName();
	@FXML private Text textMessageBox;

//	@FXML protected void handleClickButtonAction(ActionEvent event) {
//		if (textMessageBox != null) {
//			textMessageBox.setText("Controller says: clicked!");
//		}
//	}

	@FXML protected void onStartButtonClick(ActionEvent event) {
		ApplicationView.getInstance().getStage().setTitle("onStartButtonClick");
	}

	@FXML protected void onResumeButtonClick(ActionEvent event) {
		
	}

	@FXML protected void onExitButtonClick(ActionEvent event) {
		Platform.exit();
	}
}
