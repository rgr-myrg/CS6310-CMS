package edu.gatech.cms.data;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import edu.gatech.cms.course.RequestStatus;
import edu.gatech.cms.sql.RequestsTable;
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
					preparedStatement = DbHelper.getConnection().prepareStatement(RequestsTable.INSERT_SQL);
					preparedStatement.setInt(1, Integer.valueOf(parts[0]));
					preparedStatement.setInt(2, Integer.valueOf(parts[1]));
					preparedStatement.setInt(3, RequestStatus.Pending.ordinal());
					preparedStatement.execute();
				} catch (SQLException e) {
					DbHelper.logSqlException(e);
				}
			}
		}
	}

	public static final void load(final int cycleNumber) {
		// Filename format: requests_<cycle number>.csv
		final String filename = String.format(FILE_NAME, cycleNumber);
		new RequestsData(filename);
	}
}
