package edu.gatech.cms.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.gatech.cms.InputFileHandler;
import edu.gatech.cms.course.Course;
import edu.gatech.cms.logger.Log;
import edu.gatech.cms.logger.Logger;
import edu.gatech.cms.sql.CoursesTable;
import edu.gatech.cms.util.CsvDataLoader;
import edu.gatech.cms.util.DbHelper;

public class CoursesData extends CsvDataLoader {
	public static final String FILE_NAME  = "courses.csv";

	// Parse info. Ex: 8,Computer architecture,Fall,Spring or 13,Machine learning
	// Group 3 will match ---> Fall,Spring or EOL
	// (\d+),([\w+\s+]+),?([\w+,]+|$)
	private static final Pattern pattern = Pattern.compile("(\\d+),([\\w+\\s+]+),?([\\w+,]+|$)");

	public CoursesData() {
		super(FILE_NAME);
	}

	/**
	 * Load courses from CSV (rows) into DB and memory.
	 */
	@Override
	public void populateCsvDataToDb(final String[] rawDataArray) {
		if (rawDataArray == null || rawDataArray.length == 0) {
			return;
		}

		for (String line : rawDataArray) {
			Matcher match = pattern.matcher(line);

			if (match.find() && !match.group().isEmpty()) {
				//System.out.println("----> " + match.group(1) + ":" + match.group(2) + ":" + match.group(3));
				try {
				    
				    Integer id = Integer.valueOf(match.group(1));
				    String name = match.group(2);
				    String semesters = match.group(3);
				    
				    // insert in db
				    PreparedStatement preparedStatement = DbHelper.getConnection().prepareStatement(CoursesTable.INSERT_SQL);
					preparedStatement.setInt(1, id);
					preparedStatement.setString(2, name);
					preparedStatement.setString(3, semesters);
					// (will ignore semesters for now)
					preparedStatement.execute();
					
					// save in memory as well
					Course course = new Course(id, name, "");
					InputFileHandler.getCourses().put(id, course);

					if (Log.isDebug()) {
						Logger.debug(TAG, "Loaded from CSV - " + course);
					}
					
				} catch (SQLException e) {
					DbHelper.logSqlException(e);
				}
			}
		}
	}

	/**
	 * Load a CSV file with courses.
	 */
	public static final void loadFromCSV() {
		new CoursesData();
	}
	
	public static final void loadFromDB() {
		try {
			PreparedStatement preparedStatement = DbHelper.getConnection().prepareStatement(CoursesTable.SELECT_SQL);
			
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
	
				int id = rs.getInt(1);
				String title = rs.getString(2);
				Course course = new Course(id, title, "");
				InputFileHandler.getCourses().put(id, course);

				if (Log.isDebug()) {
					Logger.debug(TAG, "Loaded from DB - " + course);
				}
			}
		} catch (SQLException e) {
			DbHelper.logSqlException(e);
		}
	}
}
