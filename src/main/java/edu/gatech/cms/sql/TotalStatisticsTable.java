package edu.gatech.cms.sql;

public class TotalStatisticsTable {
	public static final String TABLE_NAME = "STATISTICS_TOTAL";
	public static final String ID_COLUMN = "_id";
	public static final String STATISTIC_NAME_COLUMN = "statisticName";
	public static final String STATISTIC_TOTAL_COLUMN = "statisticTotal";
	public static final String TIMESTAMP_COLUMN = "timestamp";

	public static String EXAMINED = "Examined";
	public static String GRANTED = "Granted";
	public static String FAILED = "Failed";
	public static String WAITLISTED = "Wait Listed";

	public static final String CREATE_TABLE = String.format(
			"CREATE TABLE IF NOT EXISTS %s (" 
				+ "%s INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "%s TEXT NOT NULL,"
				+ "%s INTEGER NOT NULL DEFAULT 0,"
				+ "%s DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL"
				+ ")",
				TABLE_NAME,
				ID_COLUMN,
				STATISTIC_NAME_COLUMN,
				STATISTIC_TOTAL_COLUMN,
				TIMESTAMP_COLUMN
			);

	public static final String DROP_TABLE = String.format("DROP TABLE IF EXISTS %s", TABLE_NAME);

	public static final String SELECT_ALL = String.format("SELECT * FROM %s", TABLE_NAME);

	public static final String SELECT_TOTAL_WITH_STATISTIC_NAME = String.format(
			"%s WHERE %s = ?", 
			SELECT_ALL, 
			STATISTIC_NAME_COLUMN
	);

	public static final String INSERT_SQL = String.format(
			"INSERT INTO %s (%s, %s) VALUES (?, ?)", 
			TABLE_NAME, 
			STATISTIC_NAME_COLUMN, 
			STATISTIC_TOTAL_COLUMN
	);

	public static final String INSERT_EXAMINED = String.format(
			"INSERT INTO %s (%s) VALUES (0)", TABLE_NAME, EXAMINED
	);

	public static final String INSERT_GRANTED = String.format(
			"INSERT INTO %s (%s) VALUES (0)", TABLE_NAME, GRANTED
	);

	public static final String INSERT_FAILED = String.format(
			"INSERT INTO %s (%s) VALUES (0)", TABLE_NAME, FAILED
	);

	public static final String INSERT_WAITLISTED = String.format(
			"INSERT INTO %s (%s) VALUES (0)", TABLE_NAME, WAITLISTED
	);

	public static final String UDATE_STATISTIC_TOTAL = String.format(
			"UPDATE %s SET %s = ? WHERE %s = ?",
			TABLE_NAME,
			STATISTIC_TOTAL_COLUMN,
			STATISTIC_NAME_COLUMN
	);
}
