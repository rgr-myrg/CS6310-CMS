package edu.gatech.cms.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.gatech.cms.InputFileHandler;
import edu.gatech.cms.course.Course;
import edu.gatech.cms.course.Record;
import edu.gatech.cms.logger.Log;
import edu.gatech.cms.logger.Logger;
import edu.gatech.cms.sql.RecordsTable;
import edu.gatech.cms.university.Instructor;
import edu.gatech.cms.university.Student;
import edu.gatech.cms.util.CsvDataLoader;
import edu.gatech.cms.util.DbHelper;

public class RecordsData extends CsvDataLoader {
	public static final String FILE_NAME = "records.csv";

	public RecordsData() {
		super(FILE_NAME);
	}

	/**
	 * Load from CSV (rows) into DB and memory.
	 */
	@Override
	public void populateCsvDataToDb(final String[] rawDataArray) {

		// reset records in memory
		InputFileHandler.resetRecords();
		
		if (rawDataArray.length == 0) {
			return;
		}

		// student uuid, course id, instructor id, comments, letter grade
		// 16,8,3,completes work with quality in mind,D

		for (String line : rawDataArray) {
			String[] parts = line.split(",");
			if (parts.length > 0) {
				try {
				    
				    Integer studentId = Integer.valueOf(parts[0]);
				    Integer courseId = Integer.valueOf(parts[1]);
				    Integer instructorId = Integer.valueOf(parts[2]);
				    String comments = parts[3];
				    String letterGrade = parts[4];
				    
				    // load the record in db
				    PreparedStatement preparedStatement = DbHelper.getConnection().prepareStatement(RecordsTable.INSERT_SQL);
					preparedStatement.setInt(1, studentId);
					preparedStatement.setInt(2, courseId);
					preparedStatement.setInt(3, instructorId);
					preparedStatement.setString(4, comments);
					preparedStatement.setString(5, letterGrade);
					// run it
					preparedStatement.execute();
					
					// create also a Record object in memory
					Record record = new Record(
					        InputFileHandler.getStudents().get(studentId),
                            InputFileHandler.getCourses().get(courseId),
                            InputFileHandler.getInstructors().get(instructorId),
                            comments, 
                            letterGrade
					        );
					// add the record to the student's list
					record.getStudent().addRecord(record);
					// add the record to the "big list of records"
					InputFileHandler.getRecords().add(record);

					if (Log.isDebug()) {
						Logger.debug(TAG, "Loaded from CSV - " + record);
					}
					
				} catch (SQLException e) {
					DbHelper.logSqlException(e);
				}
			}
		}
	}

	/**
	 * Load CSV with records.
	 */
	public static final void loadFromCSV() {
		new RecordsData();
	}
	
	/**
	 * Load from DB (resume option).
	 */
	public static final void loadFromDB() {
		try {
			PreparedStatement preparedStatement = DbHelper.getConnection().prepareStatement(RecordsTable.SELECT_SQL);
			
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
	
				Student student = InputFileHandler.getStudents().get(rs.getInt(2));
				Course course = InputFileHandler.getCourses().get(rs.getInt(3));
				Instructor instructor = InputFileHandler.getInstructors().get(rs.getInt(4));
				String comments = rs.getString(5);
				String gradeLetter = rs.getString(6);
				
				Record record = new Record(student, course, instructor, comments, gradeLetter);
				InputFileHandler.getRecords().add(record);
				student.addRecord(record);

				if (Log.isDebug()) {
					Logger.debug(TAG, "Loaded from DB - " + record);
				}
			}
		} catch (SQLException e) {
			DbHelper.logSqlException(e);
		}
	}
	
	/**
	 * Save a new Record in DB. 
	 * @param record
	 */
	public static void save(Record record) {
		try {
			PreparedStatement preparedStatement = DbHelper.getConnection().prepareStatement(RecordsTable.INSERT_SQL);

			preparedStatement.setInt(1, record.getStudent().getUUID());
			preparedStatement.setInt(2, record.getCourse().getID());
			preparedStatement.setInt(3, record.getInstructor().getUUID());
			preparedStatement.setString(4, record.getInstructorComments());
			preparedStatement.setString(5, record.getGradeEarned());

			preparedStatement.execute();
			
			if (Log.isDebug()) {
				Logger.debug(TAG, "Saved record in DB - " + record);
			}
			
		} catch (SQLException e) {
			DbHelper.logSqlException(e);
		}

	}
}
