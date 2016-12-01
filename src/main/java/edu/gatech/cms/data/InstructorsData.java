package edu.gatech.cms.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.gatech.cms.InputFileHandler;
import edu.gatech.cms.logger.Log;
import edu.gatech.cms.logger.Logger;
import edu.gatech.cms.sql.UniversityPersonTable;
import edu.gatech.cms.university.Instructor;
import edu.gatech.cms.university.UniversityPersonRole;
import edu.gatech.cms.util.CsvDataLoader;
import edu.gatech.cms.util.DbHelper;

public class InstructorsData extends CsvDataLoader {
	public static final String FILE_NAME = "instructors.csv";

	public InstructorsData() {
		super(FILE_NAME);
	}

	@Override
	public void populateCsvDataToDb(final String[] rawDataArray) {
		if (rawDataArray.length == 0) {
			return;
		}

		PreparedStatement preparedStatement = null;

		// uuid, name, address, phone
		// 2,EVERETT KIM,699 Sheffield Drive 59251,8041174317

		for (String line : rawDataArray) {
			String[] parts = line.split(",");
			if (parts.length > 0) {
				try {
				    
				    Integer id = Integer.valueOf(parts[0]);
				    String name = parts[1];
				    String address = parts[2];
				    String phone = parts[3];
				    
				    // insert in db
					preparedStatement = DbHelper.getConnection().prepareStatement(UniversityPersonTable.INSERT_SQL);
					preparedStatement.setInt(1, id);
					preparedStatement.setString(2, name);
					preparedStatement.setString(3, address);
					preparedStatement.setString(4, phone);
					preparedStatement.setInt(5, UniversityPersonRole.Instructor.ordinal());
					// run it
					preparedStatement.execute();
					
					// keep in memory
					Instructor instructor = new Instructor(id, name, address, phone);
					InputFileHandler.getInstructors().put(id, instructor);

					if (Log.isDebug()) {
						Logger.debug(TAG, "Loaded from CSV - " + instructor);
					}
					
				} catch (SQLException e) {
					DbHelper.logSqlException(e);
				}
			}
		}
	}

	public static final void loadFromCSV() {
		new InstructorsData();
	}
	
	public static final void loadFromDB() {
		try {
			PreparedStatement preparedStatement = DbHelper.getConnection().prepareStatement(UniversityPersonTable.SELECT_INSTRUCTORS);
			
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
	
				int id = rs.getInt(2);
				String name = rs.getString(3);
				String address = rs.getString(4);
				String phone = rs.getString(5);
				Instructor instructor = new Instructor(id, name, address, phone);
				InputFileHandler.getInstructors().put(id, instructor);

				if (Log.isDebug()) {
					Logger.debug(TAG, "Loaded from DB - " + instructor);
				}
			}
		} catch (SQLException e) {
			DbHelper.logSqlException(e);
		}
	}
}
