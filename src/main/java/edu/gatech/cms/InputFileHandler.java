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
	
	//Note: semester stats must be reset each cycle, total stats will not be reset
	private static Map<String, Integer> semesterStatistics = new TreeMap<>();
	private static Map<String, Integer> totalStatistics = new TreeMap<>();
	private static String examinedText = "Examined";
	private static String grantedText = "Granted";
	private static String failedText = "Failed";
	private static String waitlistText = "Wait Listed";

	private static WekaDataSource wekaDataSource = null;

	public static enum UiMode {
		INITIAL, 
		RESUME
	};

	/**
	 * This method is invoked by the ui when the app starts.
	 */
	public static void loadFromCSV() {
		
		extractSemester();
		instantiateStatsTreeMaps();

		if (Log.isDebug()) {
			Logger.debug(TAG, "loadFromCSV currentSemester: " + currentSemester);
		}

		// INITIAL MODE
		if (currentSemester == 0) {
			DbHelper.dropTables();
			DbHelper.createTables();

			//load from CSV then load from db
			StudentsData.loadFromCSV();
			CoursesData.loadFromCSV();
			PrerequisitesData.loadFromCSV();
			InstructorsData.loadFromCSV();
			RecordsData.loadFromCSV();
			
			StudentsData.loadFromDB();
			CoursesData.loadFromDB();
			PrerequisitesData.loadFromDB();
			InstructorsData.loadFromDB();
			RecordsData.loadFromDB();
			
			currentSemester = 1;
		}
		
		// currentSemester > 0 indicates 'resume' mode
		else {
			//clear out 'semesterStatistics' counts
			for(Map.Entry<String,Integer> statCategory : semesterStatistics.entrySet()) {
				  statCategory.setValue(0);
			}
			
			//increment previous semester
			currentSemester++;
		}
		
		//TODO: update new semester back into DB
		
		//Load remaining files now that we know the semester
		AssignmentsData.load(currentSemester);
		RequestsData.load(currentSemester);
	}
	
	//This method will process all requests and assumes requests List already loaded
	public static void processRequests() {
		List<Request> requestsList = requests.get(currentSemester);
		
		for(Request r : requestsList) {
			r.getStudent().enrollInCourse(r.getCourse());
			
			//log the status for each student request in debug mode
			if (Log.isDebug()) {
				Logger.debug(TAG, "Request from: " + r.getStudent().getFullName() + " is " + r.getStatus().name());
			}
		}
		
		//TODO: spruce up this output text if we would like it to differ from the assignment spec
		System.out.println("Processed Requests");
		for(Request r : requestsList) {
			System.out.println("request (" + r.getStudent().getUUID() + ", " + r.getCourse().getID() + "): " + r.getReason());
		}
	}
	
	//This method will print all academic records
	public static void printAcademicRecords(){
		System.out.println("Academic Records");
		for(Record r : records) {
			System.out.print(r.getStudent().getUUID() + ", ");
			System.out.print(r.getCourse().getID() + ", ");
			System.out.print(r.getInstructor().getUUID() + ", ");
			System.out.print(r.getInstructorComments() + ", ");
			System.out.println(r.getGradeEarned());
		}
	}
	
	private static void getSemesterStats() {
		System.out.println("Semester Statistics");
		System.out.print(examinedText + ": " + semesterStatistics.get(examinedText) + " ");
		System.out.print(grantedText + ": " + semesterStatistics.get(grantedText) + " ");
		System.out.print(failedText + ": " + semesterStatistics.get(grantedText) + " ");
		System.out.println(waitlistText + ": " + semesterStatistics.get(grantedText));
		
		System.out.println("Total Statistics");
		System.out.print(examinedText + ": " + totalStatistics.get(examinedText) + " ");
		System.out.print(grantedText + ": " + totalStatistics.get(grantedText) + " ");
		System.out.print(failedText + ": " + totalStatistics.get(grantedText) + " ");
		System.out.println(waitlistText + ": " + totalStatistics.get(grantedText));
	}

	private static void extractSemester() {
		// Select current semester from db. 
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
			StudentsData.loadFromCSV();
			InstructorsData.loadFromCSV();
			CoursesData.loadFromCSV();
			PrerequisitesData.loadFromCSV();
			RecordsData.loadFromCSV();
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

		// Load requests and assignments for each semester
		RequestsData.load(currentSemester);
		AssignmentsData.load(currentSemester);

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

	private static void instantiateStatsTreeMaps(){
		semesterStatistics.put(examinedText, 0);
		semesterStatistics.put(grantedText, 0);
		semesterStatistics.put(failedText, 0);
		semesterStatistics.put(waitlistText, 0);
		
		totalStatistics.put(examinedText, 0);
		totalStatistics.put(grantedText, 0);
		totalStatistics.put(failedText, 0);
		totalStatistics.put(waitlistText, 0);
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

	public void calculateCapacityForCourse() {
		
	}

	public void lockAssignmentsForSemester() {
	
	}		

	public static void calculateCapacityForCourser() {

	}

	public static void loackAssignmentsForSemester() {

	}

	public static void validateStudentRequests() {

	}

	// SIMPLE SETTERS, GETTERS for model objects

	public static void incrementSemesterStats(String keyString) {
		Integer newInt = semesterStatistics.get(keyString) +  1;
		semesterStatistics.put(keyString, newInt);
	}
	
	public static void incrementTotalStats(String keyString) {
		Integer newInt = totalStatistics.get(keyString) +  1;
		totalStatistics.put(keyString, newInt);
	}
	
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
	
	public static void addRequest(Request request) {
		List<Request> requestsList = requests.get(currentSemester);
		requestsList.add(request);
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
	
	public static String getExaminedText() {
		return examinedText;
	}

	public static void setExaminedText(String examinedText) {
		InputFileHandler.examinedText = examinedText;
	}

	public static String getGrantedText() {
		return grantedText;
	}

	public static void setGrantedText(String grantedText) {
		InputFileHandler.grantedText = grantedText;
	}

	public static String getFailedText() {
		return failedText;
	}

	public static void setFailedText(String failedText) {
		InputFileHandler.failedText = failedText;
	}

	public static String getWaitlistText() {
		return waitlistText;
	}

	public static void setWaitlistText(String waitlistText) {
		InputFileHandler.waitlistText = waitlistText;
	}
}
