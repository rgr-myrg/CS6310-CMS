package edu.gatech.cms.sql;

import edu.gatech.cms.course.RequestStatus;

public class RequestsTable {
	public static final String TABLE_NAME = "REQUESTS";

	public static final String ID_COLUMN = "_id";
	public static final String STUDENT_ID_COLUMN  = "studentUuid";
	public static final String COURSE_ID_COLUMN = "courseId";
	public static final String REQUEST_STATUS_COLUMN = "requestStatus";
	public static final String STATUS_REASON_COLUMN = "statusReason";
	public static final String SEMESTER_COLUMN = "semesterIndex";

	public static final int OPEN_REQUEST_DEFAULT_VALUE = -1;

	public static final String CREATE_TABLE = String.format(
			"CREATE TABLE IF NOT EXISTS %s (" 
					+ "%s INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "%s INTEGER NOT NULL,"
					+ "%s INTEGER NOT NULL,"
					+ "%s INTEGER NOT NULL DEFAULT 0,"
					+ "%s INTEGER NOT NULL DEFAULT %d,"
					+ "%s TEXT,"
					+ "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL"
					+ ")",
			TABLE_NAME, 
			ID_COLUMN, 
			STUDENT_ID_COLUMN, 
			COURSE_ID_COLUMN, 
			SEMESTER_COLUMN,
			REQUEST_STATUS_COLUMN, OPEN_REQUEST_DEFAULT_VALUE, 
			STATUS_REASON_COLUMN
	);

	public static final String DROP_TABLE = String.format("DROP TABLE IF EXISTS %s", TABLE_NAME);

	public static final String INSERT_SQL = String.format(
			"INSERT INTO %s (%s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?)", 
			TABLE_NAME, STUDENT_ID_COLUMN, COURSE_ID_COLUMN, SEMESTER_COLUMN, REQUEST_STATUS_COLUMN, STATUS_REASON_COLUMN
	);

	public static final String SELECT_MAX_SEMESTER = String.format(
			"SELECT MAX(%s) AS %s FROM %s WHERE %s != %d OR %s != ''", 
			SEMESTER_COLUMN, SEMESTER_COLUMN ,TABLE_NAME, REQUEST_STATUS_COLUMN, RequestStatus.Pending.ordinal(), STATUS_REASON_COLUMN);

	public static final String SELECT_COUNT = String.format(
			"SELECT COUNT(%s) AS total FROM %s", 
			ID_COLUMN, TABLE_NAME
	);

	public static final String SELECT_REQUESTS = String.format(
			"SELECT %s, %s FROM %s",
			STUDENT_ID_COLUMN, COURSE_ID_COLUMN, TABLE_NAME
			);

	public static final String SELECT_ALL_REQUESTS = String.format(
			"SELECT DISTINCT(*) FROM %s",
			TABLE_NAME
			);

	public static final String SELECT_OPEN_REQUESTS = String.format(
			"SELECT DISTINCT(*) FROM %s WHERE %s = %d",
			TABLE_NAME, REQUEST_STATUS_COLUMN, OPEN_REQUEST_DEFAULT_VALUE
	);

	public static final String SELECT_APPROVED_REQUESTS = String.format(
			"SELECT * FROM %s WHERE %s = %d ORDER BY _id",
			TABLE_NAME, REQUEST_STATUS_COLUMN, RequestStatus.Accepted.ordinal()
	);

	public static final String SELECT_WAITING_REQUESTS = String.format(
			"SELECT DISTINCT(*) FROM %s WHERE %s = %d ORDER BY _id",
			TABLE_NAME, STATUS_REASON_COLUMN, RequestStatus.RejectedFullCapacity.ordinal()
	);

	public static final String SELECT_APPROVED_REQUESTS_INFO = String.format(
			"SELECT REQUESTS.studentUuid AS userUuid, REQUESTS.courseId AS courseId, "
				+ "UNIVERSITY_PERSON.name AS name, COURSES.courseTitle AS courseTitle "
				+ "FROM REQUESTS "
				+ "LEFT JOIN UNIVERSITY_PERSON ON REQUESTS.studentUuid = USERS.userUuid "
				+ "LEFT JOIN COURSES ON REQUESTS.courseId = COURSES.courseId "
				+ "WHERE %s = %d AND %s = ? ORDER BY REQUESTS.timestamp",
			REQUEST_STATUS_COLUMN, RequestStatus.Accepted.ordinal(), SEMESTER_COLUMN
	);

	public static final String SELECT_REQUEST_BY_STUDENT_ID_AND_COURSE_ID = String.format(
			"SELECT * FROM %s WHERE %s = ? AND %s = ?",
			TABLE_NAME, STUDENT_ID_COLUMN, COURSE_ID_COLUMN
	);

	public static final String UPDATE_REQUESTS_WITH_DENIED_REASON = String.format(
			"UPDATE %s SET %s = ? WHERE %s = ? AND %s = ?",
			TABLE_NAME, STATUS_REASON_COLUMN, STUDENT_ID_COLUMN, COURSE_ID_COLUMN
	);
	
	public static final String UPDATE_REQUESTS_TO_ACCEPTED = String.format(
			"UPDATE %s SET %s = %d, %s = ?, %s = ?, timestamp = CURRENT_TIMESTAMP " 
				+ "WHERE %s = ? AND %s = ?",
			TABLE_NAME, REQUEST_STATUS_COLUMN, RequestStatus.Accepted.ordinal(),
			SEMESTER_COLUMN,
			STATUS_REASON_COLUMN, 
			STUDENT_ID_COLUMN, COURSE_ID_COLUMN
	);

}

/*
The data provided in the requests.csv file represents the requests from students to take specific
courses as listed in the catalog. Each record has two fields: simply, (1) the ID of the student; and, (2)
the ID of the course being requested. As discussed in the previous design sessions, there are various
reasons why a specific course request might not be granted. Your system must use the information
provided in the various files to determine if each request is valid or not based on the various
requirements. For example, the first line of the file below represents student 9 (Gary Allen)
requesting to take course 13 (Machine Learning). Here is an example of the requests.csv file:
9,13
16,29
15,29
*/