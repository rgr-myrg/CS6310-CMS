package edu.gatech.cms.sql;

public class CoursesTable {
	public static final String TABLE_NAME = "COURSES";
	public static final String ID_COLUMN = "_id";
	public static final String COURSE_ID_COLUMN = "courseId";
	public static final String COURSE_TITLE_COLUMN = "courseTitle";
	public static final String SEMESTERS_COLUMN = "semesters";
	public static final String TIMESTAMP_COLUMN = "timestamp";

	public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " 
			+ TABLE_NAME 
				+ "(" 
				+ ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT," 
				+ COURSE_ID_COLUMN + " INTEGER NOT NULL," 
				+ COURSE_TITLE_COLUMN + " TEXT NOT NULL," 
				+ SEMESTERS_COLUMN + " TEXT," 
				+ TIMESTAMP_COLUMN + " DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL," 
				+ "UNIQUE (" + COURSE_TITLE_COLUMN + ") ON CONFLICT REPLACE" 
				+ ")";

	public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

	public static final String INSERT_SQL = "INSERT INTO " + TABLE_NAME 
			+ "(" 
				+ COURSE_ID_COLUMN + ","
				+ COURSE_TITLE_COLUMN + "," 
				+ SEMESTERS_COLUMN 
			+ ") VALUES (?, ?, ?)";

	public static final String SELECT_SQL = "SELECT " 
			+ COURSE_ID_COLUMN + "," 
			+ COURSE_TITLE_COLUMN + ","
			+ SEMESTERS_COLUMN + " FROM " + TABLE_NAME;

	public static final String SELECT_COUNT = "SELECT COUNT(*) AS total FROM " + TABLE_NAME;

	public static final String SELECT_COUNT_WITHOUT_STUDENTS = SELECT_COUNT 
			+ " WHERE " + COURSE_ID_COLUMN 
			+ " NOT IN ( "
				+ "SELECT " + RecordsTable.COURSE_ID_COLUMN + " FROM " + RecordsTable.TABLE_NAME 
				+ " )";

	public static final String SELECT_COUNT_FOR_FALL_COURSES = SELECT_COUNT 
			+ " WHERE " + SEMESTERS_COLUMN
			+ " LIKE '%Fall%'";

	public static final String SELECT_COUNT_FOR_SPRING_COURSES = SELECT_COUNT 
			+ " WHERE " + SEMESTERS_COLUMN
			+ " LIKE '%Spring%'";

	public static final String SELECT_COUNT_FOR_SUMMER_COURSES = SELECT_COUNT 
			+ " WHERE " + SEMESTERS_COLUMN
			+ " LIKE '%Summer%'";
}
