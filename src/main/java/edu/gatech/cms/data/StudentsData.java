package edu.gatech.cms.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.gatech.cms.InputFileHandler;
import edu.gatech.cms.logger.Log;
import edu.gatech.cms.logger.Logger;
import edu.gatech.cms.sql.UniversityPersonTable;
import edu.gatech.cms.university.Student;
import edu.gatech.cms.university.UniversityPersonRole;
import edu.gatech.cms.util.CsvDataLoader;
import edu.gatech.cms.util.DbHelper;

public class StudentsData extends CsvDataLoader {
	public static final String FILE_NAME = "students.csv";

	public StudentsData() {
		super(FILE_NAME);
	}

	/**
	 * Load CSV with students (rows) into DB and memory.
	 */
	@Override
	public void populateCsvDataToDb(final String[] rawDataArray) {
		if (rawDataArray.length == 0) {
			return;
		}

		// uuid, name, address, phone
		// 8,REBECCA CURRY,692 Ashley Court 92876,9636667844

		for (String line : rawDataArray) {
			String[] parts = line.split(",");
			if (parts.length > 0) {
				try {
				    
				    // interpret the line
				    Integer id = Integer.valueOf(parts[0]);
				    String name = parts[1];
				    String address = parts[2];
				    String phone = parts[3];
				    
                    // insert in db
				    PreparedStatement preparedStatement = DbHelper.getConnection().prepareStatement(UniversityPersonTable.INSERT_SQL);
					preparedStatement.setInt(1, id);
					preparedStatement.setString(2, name);
					preparedStatement.setString(3, address);
					preparedStatement.setString(4, phone);
					preparedStatement.setInt(5, UniversityPersonRole.Student.ordinal());
					// run it
					preparedStatement.execute();
					
					// keep in memory
					Student student = new Student(id, name, address, phone);
					InputFileHandler.getStudents().put(id, student);

					if (Log.isDebug()) {
						Logger.debug(TAG, "Loaded from CSV - " + student);
					}
					
				} catch (SQLException e) {
					DbHelper.logSqlException(e);
				}
			}
		}
	}

	/**
	 * Load CSV file. 
	 */
	public static final void loadFromCSV() {
		new StudentsData();
	}
	
	/**
	 * Load students from DB (resume option).
	 */
	public static final void loadFromDB() {
		try {
			PreparedStatement preparedStatement = DbHelper.getConnection().prepareStatement(UniversityPersonTable.SELECT_STUDENTS);
			
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
	
				int id = rs.getInt(2);
				String name = rs.getString(3);
				String address = rs.getString(4);
				String phone = rs.getString(5);
				Student student = new Student(id, name, address, phone);
				InputFileHandler.getStudents().put(id, student);

				if (Log.isDebug()) {
					Logger.debug(TAG, "Loaded from DB - " + student);
				}
			}
		} catch (SQLException e) {
			DbHelper.logSqlException(e);
		}
	}
}
