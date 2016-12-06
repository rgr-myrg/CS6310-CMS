package edu.gatech.cms.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {
	public static final void writeToFile(final String contents, final String filename) throws IOException {
		final FileWriter writer = new FileWriter(new File(filename));

		try {
			writer.write(contents);
		} finally {
			writer.close();
		}
	}

	public static final String readFile(final String fileName) throws IOException {
		final BufferedReader reader = new BufferedReader(new FileReader(fileName));
		final StringBuilder builder = new StringBuilder();

		try {
			String line = reader.readLine();

			while (line != null) {
				builder.append(line);
				builder.append(System.lineSeparator());
				line = reader.readLine();
			}	
		} finally {
			reader.close();
		}

		return builder.toString();
	}
}
