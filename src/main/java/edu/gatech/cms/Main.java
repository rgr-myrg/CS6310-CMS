package edu.gatech.cms;

import java.io.IOException;

import edu.gatech.cms.logger.Log;
import edu.gatech.cms.view.ApplicationView;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
	public static final String WELCOME_SCREEN = "/fxml/Welcome.fxml";
	public static final int SCENE_WIDTH  = 600;
	public static final int SCENE_HEIGHT = 400;

	@Override
	public void start(Stage stage) throws IOException {
		ApplicationView.getInstance().onAppStart(stage);
	}

	public static void main(String[] args) {
		if (args != null && args.length > 0 && args[0] != null) {
			final String option = args[0];
			Log.setDebug(option.equalsIgnoreCase("debug"));
		}

		launch(args);
	}
}
