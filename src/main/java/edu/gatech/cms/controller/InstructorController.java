package edu.gatech.cms.controller;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import edu.gatech.cms.InputFileHandler;
import edu.gatech.cms.view.ApplicationView;
import edu.gatech.cms.view.UiMessages;
import edu.gatech.cms.course.Assignment;
import edu.gatech.cms.logger.Log;
import edu.gatech.cms.logger.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.TextArea;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.application.Platform;

public class InstructorController implements ScreenController{
	@FXML private Group progressGroup;
	@FXML private ImageView progressGif;
	@FXML private ListView availableListView;
	@FXML private ListView addedListView;
	@FXML private Text welcomeText;

	public static final String TAG = InputFileHandler.class.getSimpleName();
	private List<String> selectedIndices = new ArrayList<String>();

	public InstructorController(){
		ApplicationView.getInstance().onInstructorResultsLoaded = () -> {
			progressGroup.getChildren().remove(progressGif);

			final List<Assignment> assignments = InputFileHandler.getAssignments(InputFileHandler.getCurrentSemester());
			Logger.debug(TAG, "Assignments: " + assignments);

			availableListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			addedListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	        
	        List<String> items = Arrays.asList("One", "Two", "Three");        
	        availableListView.setItems(FXCollections.observableArrayList(items));

			final String screenMsg = String.format(UiMessages.INSTRUCTOR_HEADING);
	        welcomeText.setText(screenMsg);

			availableListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			    @Override
			    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			        if(newValue != null) {
			        	Platform.runLater(() -> {
				        	addedListView.getSelectionModel().select(-1);
			        	});
			        	Logger.debug(TAG, "Clicked item: " + newValue);				        	
			        }
		    	}
		    });
			
			addedListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			    @Override
			    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				    if(newValue != null) {
				        Platform.runLater(() -> {
							availableListView.getSelectionModel().select(-1);
				        });    	
				    }			        
			        Logger.debug(TAG, "Clicked item: " + newValue);
		    	}
		    });		    
		};
	}

	@FXML protected void onAboutMenuSelected(ActionEvent event) {
		ApplicationView.getInstance().displayAppInfoAlertDialog();
	}

	@FXML protected void onNextButtonClick(ActionEvent event) {
		ApplicationView.getInstance().onInstructorControllerNextAction();
	}	

	@FXML protected void onAddButtonClick(ActionEvent event) {
		ObservableList<String> selectedItems = availableListView.getSelectionModel().getSelectedItems();
		addedListView.getItems().addAll(selectedItems);
		availableListView.getItems().removeAll(selectedItems);
	}

	@FXML protected void onRemoveButtonClick(ActionEvent event) {
		ObservableList<String> selectedItems = addedListView.getSelectionModel().getSelectedItems();
		availableListView.getItems().addAll(selectedItems);
		addedListView.getItems().removeAll(selectedItems);
	}

	@FXML protected void onClearButtonClick(ActionEvent event) {
		availableListView.getItems().clear();
		addedListView.getItems().clear();
		// Call method to reaquire data here
		List<String> items = Arrays.asList("One", "Two", "Three");        
	    availableListView.setItems(FXCollections.observableArrayList(items));		
	}

	@Override
	public Stage getStage() {
		return (Stage) progressGroup.getScene().getWindow();
	}
}