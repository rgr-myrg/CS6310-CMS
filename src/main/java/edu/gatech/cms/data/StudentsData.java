package edu.gatech.cms.data;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import edu.gatech.cms.sql.UniversityPersonTable;
import edu.gatech.cms.university.UniversityPersonRole;
import edu.gatech.cms.util.CsvDataLoader;
import edu.gatech.cms.util.DbHelper;

public class StudentsData extends CsvDataLoader {
	public static final String FILE_NAME = "students.csv";

	public StudentsData() {
		super(FILE_NAME);
	}

	@Override
	public void populateCsvDataToDb(final String[] rawDataArray) {
		if (rawDataArray.length == 0) {
			return;
		}

		PreparedStatement preparedStatement = null;

		// uuid, name, address, phone
		// 8,REBECCA CURRY,692 Ashley Court 92876,9636667844

		for (String line : rawDataArray) {
			String[] parts = line.split(",");
			if (parts.length > 0) {
				try {
					preparedStatement = DbHelper.getConnection().prepareStatement(UniversityPersonTable.INSERT_SQL);
					preparedStatement.setInt(1, Integer.valueOf(parts[0]));
					preparedStatement.setString(2, parts[1]);
					preparedStatement.setString(3, parts[2]);
					preparedStatement.setString(4, parts[3]);
					preparedStatement.setInt(5, UniversityPersonRole.Student.ordinal());

					preparedStatement.execute();
				} catch (SQLException e) {
					DbHelper.logSqlException(e);
				}
			}
		}
	}

	public static final void load() {
		new StudentsData();
	}
}
