package edu.gatech.cms.controller;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class InstructorController implements ScreenController{
	@FXML private Group progressGroup;
	@FXML private ImageView progressGif;
	@FXML private ListView availableListView;
	@FXML private ListView addedListView;
	@FXML private Text welcomeText;

	public static final String TAG = InputFileHandler.class.getSimpleName();

	public InstructorController(){
		ApplicationView.getInstance().onInstructorResultsLoaded = () -> {
			progressGroup.getChildren().remove(progressGif);
			availableListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
			addedListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	        
	        //List<String> items = Arrays.asList("One", "Two", "Three");        
	        //availableListView.setItems(FXCollections.observableArrayList(items));

			availableListView.setItems(FXCollections.observableArrayList(InputFileHandler.getAssignmentsStrings(InputFileHandler.getCurrentSemester())));

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
		if(addedListView.getItems().size() == 0){
			showSelectionEmptyError();
		}
		else{
			// get the selected instructors, calculate capacities, start filling requests
			ObservableList<String> selected = addedListView.getItems();
			// set the chosen assignments for the current semester
			InputFileHandler.setChosenAssignments(selected);
			InputFileHandler.processRequests();
			
			ApplicationView.getInstance().onInstructorControllerNextAction();
		}
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
	    availableListView.setItems(FXCollections.observableArrayList(InputFileHandler.getAssignmentsStrings(InputFileHandler.getCurrentSemester())));
	}

	protected void showSelectionEmptyError(){
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(UiMessages.EMPTY_SELECTION_ERROR_TITLE);
		alert.setHeaderText(UiMessages.EMPTY_SELECTION_ERROR_HEADER);
		alert.setResizable(true);
		alert.getDialogPane().setPrefSize(325, 200);		
		alert.setContentText(UiMessages.EMPTY_SELECTION_ERROR_BODY);
		alert.showAndWait();
	}

	@Override
	public Stage getStage() {
		return (Stage) progressGroup.getScene().getWindow();
	}
}