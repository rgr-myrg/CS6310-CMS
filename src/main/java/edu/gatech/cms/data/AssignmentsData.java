package edu.gatech.cms.data;

import java.sql.PreparedStatement;
import java.sql.SQLException;

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
					preparedStatement = DbHelper.getConnection().prepareStatement(AssignmentsTable.INSERT_SQL);
					preparedStatement.setInt(1, Integer.valueOf(parts[0]));
					preparedStatement.setInt(2, Integer.valueOf(parts[1]));
					preparedStatement.setInt(3, Integer.valueOf(parts[2]));

					preparedStatement.execute();
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
