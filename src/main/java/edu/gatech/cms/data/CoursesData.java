package edu.gatech.cms.data;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.gatech.cms.sql.CoursesTable;
import edu.gatech.cms.util.CsvDataLoader;
import edu.gatech.cms.util.DbHelper;

public class CoursesData extends CsvDataLoader {
	public static final String FILE_NAME  = "courses.csv";

	// Parse info. Ex: 8,Computer architecture,Fall,Spring or 13,Machine learning
	// Group 3 will match ---> Fall,Spring or EOL
	// (\d+),([\w+\s+]+),?([\w+,]+|$)
	private static final Pattern pattern = Pattern.compile("(\\d+),([\\w+\\s+]+),?([\\w+,]+|$)");

	public CoursesData() {
		super(FILE_NAME);
	}

	@Override
	public void populateCsvDataToDb(final String[] rawDataArray) {
		if (rawDataArray == null || rawDataArray.length == 0) {
			return;
		}

		PreparedStatement preparedStatement = null;

		for (String line : rawDataArray) {
			Matcher match = pattern.matcher(line);

			if (match.find() && !match.group().isEmpty()) {
				//System.out.println("----> " + match.group(1) + ":" + match.group(2) + ":" + match.group(3));
				try {
					preparedStatement = DbHelper.getConnection().prepareStatement(CoursesTable.INSERT_SQL);
					preparedStatement.setInt(1, Integer.valueOf(match.group(1)));
					preparedStatement.setString(2, match.group(2));
					preparedStatement.setString(3, match.group(3));
					preparedStatement.execute();
				} catch (SQLException e) {
					DbHelper.logSqlException(e);
				}
			}
		}
	}

	public static final void load() {
		new CoursesData();
	}
}
