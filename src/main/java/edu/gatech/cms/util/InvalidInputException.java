package edu.gatech.cms.util;

import edu.gatech.cms.view.ApplicationView;

public class InvalidInputException extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidInputException(String message) {
		super(message);
	}

	public static final InvalidInputException unableToReadFile(String fileName) {
		ApplicationView.getInstance().onUnableToReadFileException(fileName);
		return new InvalidInputException("Error: Unable to read " + fileName + ".");
	}
}
