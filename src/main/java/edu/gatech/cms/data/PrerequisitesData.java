package edu.gatech.cms.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.gatech.cms.InputFileHandler;
import edu.gatech.cms.course.Course;
import edu.gatech.cms.logger.Log;
import edu.gatech.cms.logger.Logger;
import edu.gatech.cms.sql.PrerequisitesTable;
import edu.gatech.cms.util.CsvDataLoader;
import edu.gatech.cms.util.DbHelper;

public class PrerequisitesData extends CsvDataLoader {
	public static final String FILE_NAME = "prereqs.csv";

	public PrerequisitesData() {
		super(FILE_NAME);
	}

	@Override
	public void populateCsvDataToDb(final String[] rawDataArray) {
		if (rawDataArray.length == 0) {
			return;
		}

		PreparedStatement preparedStatement = null;

		// preReqCourseId, courseId
		// 2,10

		for (String line : rawDataArray) {
			String[] parts = line.split(",");
			if (parts.length > 0) {
				try {
					
					Integer prereqId = Integer.valueOf(parts[0]);
					Integer courseId = Integer.valueOf(parts[1]);
					preparedStatement = DbHelper.getConnection().prepareStatement(PrerequisitesTable.INSERT_SQL);
					preparedStatement.setInt(1, prereqId);
					preparedStatement.setInt(2, courseId);

					preparedStatement.execute();
					
					// add to model
					Course prereq = InputFileHandler.getCourses().get(prereqId);
					Course course = InputFileHandler.getCourses().get(courseId);
					course.addPrerequisite(prereq);

					if (Log.isDebug()) {
						Logger.debug(TAG, "Loaded from CSV - Prereq - " + prereqId + ", " + courseId);
					}
					
				} catch (SQLException e) {
					DbHelper.logSqlException(e);
				}
			}
		}
	}

	public static final void loadFromCSV() {
		new PrerequisitesData();
	}
	
	public static final void loadFromDB() {
		try {
			PreparedStatement preparedStatement = DbHelper.getConnection().prepareStatement(PrerequisitesTable.SELECT_PREREQUISITES);
			
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				Integer prereqId = Integer.valueOf(rs.getInt(2));
				Integer courseId = Integer.valueOf(rs.getInt(3));
				
				// add to model
				Course prereq = InputFileHandler.getCourses().get(prereqId);
				Course course = InputFileHandler.getCourses().get(courseId);
				course.addPrerequisite(prereq);

				if (Log.isDebug()) {
					Logger.debug(TAG, "Loaded from DB - Prereq - " + prereqId + ", " + courseId);
				}
			}
		} catch (SQLException e) {
			DbHelper.logSqlException(e);
		}
	}
}
