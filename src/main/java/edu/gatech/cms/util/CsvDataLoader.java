package edu.gatech.cms.util;

import java.io.IOException;

import edu.gatech.cms.logger.Log;
import edu.gatech.cms.logger.Logger;

public class CsvDataLoader {
	public static final String TAG = CsvDataLoader.class.getSimpleName();
	public CsvDataLoader(final String fileName) {
		String[] rawDataArray = null;

		try {
			rawDataArray = loadDataFromFile(fileName);
		} catch (InvalidInputException e) {
			if (Log.isDebug()) {
				Logger.debug(TAG, e.getMessage());
			}
		}

		if (rawDataArray != null) {
			populateCsvDataToDb(rawDataArray);
		}
	}

	private final String[] loadDataFromFile(final String fileName) throws InvalidInputException {
		String rawData = null;

		try {
			rawData = FileUtil.readFile(fileName);
		} catch (IOException e) {
			if (Log.isDebug()) {
				Logger.debug(TAG, e.getMessage());
			}

			rawData = null;
		}

		if (rawData == null) {
			throw InvalidInputException.unableToReadFile(fileName);
		}

		return rawData.split("\n");
	}

	// Override in subclass if needed
	public void populateCsvDataToDb(final String[] rawDataArray) {}
}
