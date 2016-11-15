package edu.gatech.cms.util;

public class InvalidInputException extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidInputException(String message) {
		super(message);
	}

	public static final InvalidInputException unableToReadFile(String fileName) {
		return new InvalidInputException("Error: Unable to read " + fileName + ".");
	}
}
