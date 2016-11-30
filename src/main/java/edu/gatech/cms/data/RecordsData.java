package edu.gatech.cms.data;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import edu.gatech.cms.InputFileHandler;
import edu.gatech.cms.course.Record;
import edu.gatech.cms.logger.Log;
import edu.gatech.cms.logger.Logger;
import edu.gatech.cms.sql.RecordsTable;
import edu.gatech.cms.util.CsvDataLoader;
import edu.gatech.cms.util.DbHelper;

public class RecordsData extends CsvDataLoader {
	public static final String FILE_NAME = "records.csv";

	public RecordsData() {
		super(FILE_NAME);
	}

	@Override
	public void populateCsvDataToDb(final String[] rawDataArray) {
		if (rawDataArray.length == 0) {
			return;
		}

		PreparedStatement preparedStatement = null;

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
					preparedStatement = DbHelper.getConnection().prepareStatement(RecordsTable.INSERT_SQL);
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
						Logger.debug(TAG, "Loaded - " + record);
					}
					
				} catch (SQLException e) {
					DbHelper.logSqlException(e);
				}
			}
		}
	}

	public static final void load() {
		new RecordsData();
	}
}
