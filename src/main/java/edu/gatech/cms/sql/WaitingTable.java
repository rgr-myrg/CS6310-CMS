package edu.gatech.cms.sql;

public class WaitingTable {
	public static final String TABLE_NAME = "WAITING";

	public static final String ID_COLUMN = "_id";
	public static final String STUDENT_ID_COLUMN  = "studentUuid";
	public static final String COURSE_ID_COLUMN = "courseId";

	public static final String CREATE_TABLE = String.format(
			"CREATE TABLE IF NOT EXISTS %s (" 
				+ "%s INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "%s INTEGER NOT NULL,"
				+ "%s INTEGER NOT NULL,"
				+ "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL"
				+ ")",
				TABLE_NAME, ID_COLUMN, STUDENT_ID_COLUMN, COURSE_ID_COLUMN
	);

	public static final String DROP_TABLE = String.format("DROP TABLE IF EXISTS %s", TABLE_NAME);

	public static final String INSERT_SQL = String.format(
			"INSERT INTO %s (%s, %s) VALUES (?, ?)", 
			TABLE_NAME, STUDENT_ID_COLUMN, COURSE_ID_COLUMN
	);

	public static final String SELECT_COUNT = String.format(
			"SELECT COUNT(%s) AS total FROM %s", 
			ID_COLUMN, TABLE_NAME
	);

	public static final String DELETE_SQL = String.format(
			"DELETE FROM %s WHERE %s = ?", 
			TABLE_NAME, ID_COLUMN
	);
}

/*
 * The table holds the waiting lists for various courses. Basically has 2 important columns - student id and 
 * course id. Requests which fail with "no availability" will get here. 
 */