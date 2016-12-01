package edu.gatech.cms;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.gatech.cms.course.Assignment;
import edu.gatech.cms.course.Course;
import edu.gatech.cms.course.Record;
import edu.gatech.cms.course.Request;
import edu.gatech.cms.data.AssignmentsData;
import edu.gatech.cms.data.CoursesData;
import edu.gatech.cms.data.InstructorsData;
import edu.gatech.cms.data.PrerequisitesData;
import edu.gatech.cms.data.RecordsData;
import edu.gatech.cms.data.RequestsData;
import edu.gatech.cms.data.StudentsData;
import edu.gatech.cms.data.WekaDataSource;
import edu.gatech.cms.logger.Log;
import edu.gatech.cms.logger.Logger;
import edu.gatech.cms.sql.RequestsTable;
import edu.gatech.cms.university.Department;
import edu.gatech.cms.university.Instructor;
import edu.gatech.cms.university.Student;
import edu.gatech.cms.university.University;
import edu.gatech.cms.util.DbHelper;
import edu.gatech.cms.view.ApplicationView;

/**
 * The class takes care of most of data loading.
 */
public class InputFileHandler {
	public static final String TAG = InputFileHandler.class.getSimpleName();

	private static University university;
	private static Department department;

	private static Map<Integer, Course> courses = new TreeMap<>();					// key is Course ID
	private static Map<Integer, Student> students = new TreeMap<>();				// key is Student ID
	private static Map<Integer, Instructor> instructors = new TreeMap<>();			// key is Instructor ID
	private static List<Record> records = new ArrayList<Record>();
	
	private static Map<Integer,List<Request>> requests = new TreeMap<>();			// key is semester
	private static Map<Integer,List<Assignment>> assignments = new TreeMap<>();		// key is semester
	private static int currentSemester = 0;

	private static WekaDataSource wekaDataSource = null;

	public static enum UiMode {
		INITIAL, 
		RESUME
	};

	/**
	 * This method is invoked by the ui when the app starts.
	 */
	public static void loadFromCSV() {
		// Select current semester from db. Should be in the model somewhere.
		// TODO: Please move to the right place and replace with the method call.

		try {
			final ResultSet resultSet = DbHelper.doSql(RequestsTable.SELECT_MAX_SEMESTER);

			if (resultSet != null && resultSet.next()) {
				currentSemester = resultSet.getInt(RequestsTable.SEMESTER_COLUMN);
				resultSet.close();
			} else {
				currentSemester = 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// End select current semester from db

		// Adding a count of 2 to mock the UI. PLEASE REMOVE.
		// currentSemester = 2;

		if (Log.isDebug()) {
			Logger.debug(TAG, "loadFromCSV currentSemester: " + currentSemester);
		}

		// It's inferred a currentSemester of 0 indicates
		// the 'initial' state of the app.

		if (currentSemester == 0) {
			DbHelper.dropTables();
			DbHelper.createTables();

			StudentsData.loadFromCSV();
			InstructorsData.loadFromCSV();
			CoursesData.loadFromCSV();
			PrerequisitesData.loadFromCSV();
			RecordsData.loadFromCSV();
		}
		else {
			StudentsData.loadFromDB();
			InstructorsData.loadFromDB();
			CoursesData.loadFromDB();
			PrerequisitesData.loadFromDB();
			RecordsData.loadFromDB();
		}
	}

	/**
	 * This method is invoked after the user selects 
	 * 'initial' or 'resume' on the Welcome screen
	 * and has clicked the 'next' button.
	 */
	public static void designateSemester() {
		// Reset current semester for 'initial' mode
		if (ApplicationView.getInstance().getUiMode() == UiMode.INITIAL) {
			currentSemester = 0;
		}

		// Increment to process the next batch of files
		currentSemester++;

		if (Log.isDebug()) {
			Logger.debug(TAG, "designateSemester currentSemester: " + currentSemester);
		}
	}
	
	public static void loadAssignments() {
		// Load assignments for current semester
		List<Assignment> semAssignments = new ArrayList<>();
		assignments.put(currentSemester, semAssignments);
		AssignmentsData.load(currentSemester);
	}

	public static void loadRequests() {
		// Load requests for current semester
		List<Request> semRequests = new ArrayList<>();
		requests.put(currentSemester, semRequests);
		RequestsData.load(currentSemester);
	}

	public static void prepareDataForDataMining() {
		if (wekaDataSource == null) {
			wekaDataSource = new WekaDataSource();
		}
	}

	public static String analyzeHistoryAndRoster() {
		if (wekaDataSource == null) {
			if (Log.isDebug()) {
				Logger.debug(TAG, "Error: wekaDataSource is NULL!");
			}
			return null;
		}

		return String.valueOf(wekaDataSource.analyzeStudentRecords());
	}

	public static void calculateCapacityForCourser() {

	}

	public static void loackAssignmentsForSemester() {

	}

	public static void validateStudentRequests() {

	}

	// SIMPLE SETTERS, GETTERS for model objects

	public static University getUniversity() {
		return university;
	}

	public static void setUniversity(University uni) {
		university = uni;
	}

	public static Department getDepartment() {
		return department;
	}

	public static void setDepartment(Department dept) {
		department = dept;
	}

	public static Map<Integer, Course> getCourses() {
		return courses;
	}

	public static Map<Integer, Student> getStudents() {
		return students;
	}

	public static Map<Integer, Instructor> getInstructors() {
		return instructors;
	}

	public static List<Record> getRecords() {
		return records;
	}

	public static Map<Integer,List<Request>> getRequests() {
		return requests;
	}

	public static List<Request> getRequests(Integer semester) {
		return requests.get(semester);
	}
	
	public static Map<Integer,List<Assignment>> getAssignments() {
		return assignments;
	}

	public static List<Assignment> getAssignments(Integer semester) {
		return assignments.get(semester);
	}

	public static List<String> getAssignmentsStrings(Integer semester) {
		List<String> strings = new ArrayList<>();
		for(Assignment assign: assignments.get(semester)) {
			strings.add("Instructor " + assign.getInstructor().getFullName() 
					+ ", course " + assign.getCourse().getTitle() 
					+ ", capacity " + assign.getCapacity());
		}
		return strings;
	}

	public static int getCurrentSemester() {
		return currentSemester;
	}

	public static void setCurrentSemester(int sem) {
		currentSemester = sem;
	}
}
