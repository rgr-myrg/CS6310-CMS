package edu.gatech.cms.data;

import java.sql.PreparedStatement;
import java.sql.SQLException;

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
					preparedStatement = DbHelper.getConnection().prepareStatement(PrerequisitesTable.INSERT_SQL);
					preparedStatement.setInt(1, Integer.valueOf(parts[0]));
					preparedStatement.setInt(2, Integer.valueOf(parts[1]));

					preparedStatement.execute();
				} catch (SQLException e) {
					DbHelper.logSqlException(e);
				}
			}
		}
	}

	public static final void load() {
		new PrerequisitesData();
	}
}
