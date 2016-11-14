package edu.gatech.cms.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class WelcomeController {
	public static final String TAG = WelcomeController.class.getSimpleName();
	@FXML private Text textMessageBox;

	@FXML protected void handleClickButtonAction(ActionEvent event) {
		if (textMessageBox != null) {
			textMessageBox.setText("Controller says: clicked!");
		}
	}
}
