package edu.gatech.cms.sql;

public class PrerequisitesTable {
	public static final String TABLE_NAME = "PREREQUISITES";

	public static final String ID_COLUMN = "_id";
	public static final String PREREQ_ID_COLUMN  = "preReqCourseId";
	public static final String COURSE_ID_COLUMN  = "courseId";

	public static final String CREATE_TABLE = String.format(
			"CREATE TABLE IF NOT EXISTS %s (" 
					+ "%s INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "%s INTEGER NOT NULL,"
					+ "%s INTEGER NOT NULL,"
					+ "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL"
					+ ")",
					TABLE_NAME, ID_COLUMN, PREREQ_ID_COLUMN, COURSE_ID_COLUMN
	);

	public static final String DROP_TABLE = String.format("DROP TABLE IF EXISTS %s", TABLE_NAME);

	public static final String INSERT_SQL = String.format(
			"INSERT INTO %s (%s, %s) VALUES (?, ?)", 
			TABLE_NAME, PREREQ_ID_COLUMN, COURSE_ID_COLUMN
	);

	public static final String SELECT_COUNT = String.format(
			"SELECT COUNT(%s) AS total FROM %s", 
			ID_COLUMN, TABLE_NAME
	);

	public static final String SELECT_PREREQUISITES = String.format(
			"SELECT * FROM %s WHERE %s = ?", 
			TABLE_NAME, COURSE_ID_COLUMN
	);
}

/*
The data provided in the prereqs.csv file provides information about which courses serve as
prerequisites for other courses. Each record in this file has two fields of integers, and both field
values represent the course identifiers for courses selected from the catalog. The course ID value in
the first field means that that course serves as a prerequisite for the course ID represented in the
second field. All of the course ID values must refer to valid courses as listed in the courses.csv file.
The first line below represents the fact that course 2 (Computer Programming) is a prerequisite for
course 10 (Operating Systems). Here is an example of a prereqs.csv file:
2,10
4,17
2,20
*/