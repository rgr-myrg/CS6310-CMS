package edu.gatech.cms.controller;

import edu.gatech.cms.view.ApplicationView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class WelcomeController {
	public static final String TAG = WelcomeController.class.getSimpleName();

	@FXML private Group progressGroup;
	@FXML private ImageView progressGif;
	@FXML private Text textMessageBox;

	public WelcomeController() {
		ApplicationView.getInstance().onHideProgressBarEvent = () -> {
			progressGroup.getChildren().remove(progressGif);
		};
	}

	@FXML protected void onStartButtonClick(ActionEvent event) {
		//ApplicationView.getInstance().getStage().setTitle("onStartButtonClick");
		textMessageBox.setText("Controller says: clicked!");
		//progressGif.setVisible(false);
	}

	@FXML protected void onResumeButtonClick(ActionEvent event) {
		
	}

	@FXML protected void onExitButtonClick(ActionEvent event) {
		Platform.exit();
	}

//	public void hideProgressBar() {
//		progressGroup.getChildren().remove(progressGif);
//		//progressGif.setVisible(false);
//	}
}
