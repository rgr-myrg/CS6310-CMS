package edu.gatech.cms.data;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import edu.gatech.cms.InputFileHandler;
import edu.gatech.cms.course.Assignment;
import edu.gatech.cms.logger.Log;
import edu.gatech.cms.logger.Logger;
import edu.gatech.cms.sql.AssignmentsTable;
import edu.gatech.cms.util.CsvDataLoader;
import edu.gatech.cms.util.DbHelper;

public class AssignmentsData extends CsvDataLoader {
	public static final String FILE_NAME = "assignments_%d.csv";

	public AssignmentsData(final String filename) {
		super(filename);
	}

	@Override
	public void populateCsvDataToDb(final String[] rawDataArray) {
		if (rawDataArray.length == 0) {
			return;
		}

		PreparedStatement preparedStatement = null;

		// instructorUuid, courseId, capacity
		// 2,13,2

		for (String line : rawDataArray) {
			String[] parts = line.split(",");
			if (parts.length > 0) {
				try {
					
					Integer instructorID = Integer.valueOf(parts[0]);
					Integer courseID = Integer.valueOf(parts[1]);
					Integer capacity = Integer.valueOf(parts[2]);
				
					preparedStatement = DbHelper.getConnection().prepareStatement(AssignmentsTable.INSERT_SQL);
					preparedStatement.setInt(1, instructorID);
					preparedStatement.setInt(2, courseID);
					preparedStatement.setInt(3, capacity);

					preparedStatement.execute();
					
					// save in current semester
					Assignment assignment = new Assignment(
							InputFileHandler.getInstructors().get(instructorID),
							InputFileHandler.getCourses().get(courseID), 
							capacity);
					
					// get list of assignments for current semester
					List<Assignment> assign = InputFileHandler.getAssignments().get(InputFileHandler.getCurrentSemester());
					assign.add(assignment);
					
					if (Log.isDebug()) {
						Logger.debug(TAG, "Semester " + InputFileHandler.getCurrentSemester() + ", loaded - " + assignment);
					}
					
				} catch (SQLException e) {
					DbHelper.logSqlException(e);
				}
			}
		}
	}

	public static final void load(final int cycleNumber) {
		// Filename format: assignments_<cycle number>.csv
		final String filename = String.format(FILE_NAME, cycleNumber);
		new AssignmentsData(filename);
	}
}
