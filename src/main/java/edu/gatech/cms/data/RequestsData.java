package edu.gatech.cms.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.gatech.cms.InputFileHandler;
import edu.gatech.cms.course.Course;
import edu.gatech.cms.course.Request;
import edu.gatech.cms.course.RequestStatus;
import edu.gatech.cms.logger.Log;
import edu.gatech.cms.logger.Logger;
import edu.gatech.cms.sql.RequestsTable;
import edu.gatech.cms.university.Student;
import edu.gatech.cms.util.CsvDataLoader;
import edu.gatech.cms.util.DbHelper;

public class RequestsData extends CsvDataLoader {
	public static final String FILE_NAME = "requests_%d.csv";

	public RequestsData(final String filename) {
		super(filename);
	}

	@Override
	public void populateCsvDataToDb(final String[] rawDataArray) {
		if (rawDataArray.length == 0) {
			return;
		}

		PreparedStatement preparedStatement = null;

		// studentUuid, courseId
		// 9,13

		for (String line : rawDataArray) {
			String[] parts = line.split(",");

			if (parts.length > 0) {
				try {
					int studentId = Integer.valueOf(parts[0]);
					int courseId = Integer.valueOf(parts[1]);
					
					preparedStatement = DbHelper.getConnection().prepareStatement(RequestsTable.INSERT_SQL);
					preparedStatement.setInt(1, studentId);
					preparedStatement.setInt(2, courseId);
					preparedStatement.setInt(3, RequestStatus.Pending.ordinal());

					preparedStatement.execute();
					
					// add to the model
					Request request = new Request(InputFileHandler.getCurrentSemester(), 
							InputFileHandler.getStudents().get(studentId), 
							InputFileHandler.getCourses().get(courseId), 
							RequestStatus.Pending, 
							"");
					List<Request> requests = InputFileHandler.getRequests(InputFileHandler.getCurrentSemester());
					if (requests == null) {
						requests = new ArrayList<>();
						InputFileHandler.getRequests().put(InputFileHandler.getCurrentSemester(), requests);
					}
					requests.add(request);
					
					if (Log.isDebug()) {
						Logger.debug(TAG, "Loaded from CSV - " + request);
					}
					
				} catch (SQLException e) {
					DbHelper.logSqlException(e);
				}
			}
		}
		
		if (Log.isDebug()) {
			Logger.debug(TAG, "Loaded from CSV - " + 
					InputFileHandler.getRequests().get(InputFileHandler.getCurrentSemester()) + 
					"requests for semester" + 
					InputFileHandler.getCurrentSemester());
		}

	}

	public static final void loadFromCSV(final int semester) {
		// Filename format: requests_<semester>.csv
		final String filename = String.format(FILE_NAME, semester);
		new RequestsData(filename);
	}
	
	public static final void loadFromDB() {
		
	}

	public static final void updateRequestDenied(RequestStatus rs, Student student, Course course) {
		try {
			PreparedStatement preparedStatement = DbHelper.getConnection().prepareStatement(RequestsTable.UPDATE_REQUESTS_WITH_DENIED_REASON);
			preparedStatement.setInt(1, rs.ordinal());
			preparedStatement.setInt(2, Integer.valueOf(student.getUUID()));
			preparedStatement.setInt(3, Integer.valueOf(course.getID()));

			preparedStatement.execute();
			
			if (Log.isDebug()) {
				Logger.debug(TAG, "Saved request in DB - student: " + student.getUUID() + 
						", course: " + course.getID()  + 
						", status: " + rs);
			}

		} catch (SQLException e) {
			DbHelper.logSqlException(e);
		}
	}
	
	public static final void updateRequestAccepted(RequestStatus rs, Student student, Course course) {
		try {
			PreparedStatement preparedStatement = DbHelper.getConnection().prepareStatement(RequestsTable.UPDATE_REQUESTS_TO_ACCEPTED);
			preparedStatement.setInt(1, rs.ordinal());
			preparedStatement.setInt(2, Integer.valueOf(student.getUUID()));
			preparedStatement.setInt(3, Integer.valueOf(course.getID()));

			preparedStatement.execute();

			if (Log.isDebug()) {
				Logger.debug(TAG, "Saved request in DB - student: " + student.getUUID() + 
						", course: " + course.getID()  + 
						", status: " + rs);
			}
		} catch (SQLException e) {
			DbHelper.logSqlException(e);
		}
	}
	
	
	public static final List<Request> getWaiting() {
		List<Request> waiting = new ArrayList<>();
		try {
			
			PreparedStatement preparedStatement = DbHelper.getConnection().prepareStatement(RequestsTable.SELECT_WAITING_REQUESTS);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				Student student = InputFileHandler.getStudents().get(rs.getInt(2));
				Course course = InputFileHandler.getCourses().get(rs.getInt(3));
				int semester = rs.getInt(4);
				int status = rs.getInt(5);
				String reason = rs.getString(6);
				
				Request request = new Request(semester, student, course, RequestStatus.values()[status], reason);
				waiting.add(request);
			}
			
		} catch (SQLException e) {
			DbHelper.logSqlException(e);
		}

		return waiting;
	}
}
