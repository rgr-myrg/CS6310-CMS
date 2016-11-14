package edu.gatech.cms;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	public static final String WELCOME_SCREEN = "/fxml/Welcome.fxml";
	public static final int SCENE_WIDTH  = 500;
	public static final int SCENE_HEIGHT = 300;

	@Override
	public void start(Stage stage) throws IOException {
		final Parent parent = FXMLLoader.load(getClass().getResource(WELCOME_SCREEN));
		final Scene scene = new Scene(parent, SCENE_WIDTH, SCENE_HEIGHT);

		stage.setTitle("Team Fantastic 4 Welcome Screen");
		stage.setScene(scene);

//		stage.setMaximized(false);
//		stage.setMinHeight(500.0);
//		stage.setMinWidth(850.0);

		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
