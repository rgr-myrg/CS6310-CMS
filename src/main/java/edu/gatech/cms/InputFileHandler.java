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
import edu.gatech.cms.course.RequestStatus;
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
import edu.gatech.cms.util.GradeDistributionUtil;
import edu.gatech.cms.view.ApplicationView;

/**
 * The class takes care of most of data loading.
 */
public class InputFileHandler {
	public static final String TAG = InputFileHandler.class.getSimpleName();

	private static University university;
	private static Department department;

	private static Map<Integer, Course> courses = new TreeMap<>();				// key = Course ID
	private static Map<Integer, Student> students = new TreeMap<>();			// key = Student ID
	private static Map<Integer, Instructor> instructors = new TreeMap<>();		// key = Instructor ID
	private static List<Record> records = new ArrayList<Record>();				// all records
	
	private static Map<Integer,List<Request>> requests = new TreeMap<>();		// key = semester
	private static Map<Integer,List<Assignment>> assignments = new TreeMap<>();	// key = semester (all assignments)
	private static Map<Integer,List<Assignment>> capacities = new TreeMap<>();// key = semester (chosen assignments)
	
	
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
	public static void load() {
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

		// extractSemester();
		instantiateStatsTreeMaps();

		if (Log.isDebug()) {
			Logger.debug(TAG, "load currentSemester: " + currentSemester);
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
		}
		else {
			StudentsData.loadFromDB();
			CoursesData.loadFromDB();
			PrerequisitesData.loadFromDB();
			InstructorsData.loadFromDB();
			RecordsData.loadFromDB();
			//clear out 'semesterStatistics' counts
			for(Map.Entry<String,Integer> statCategory : semesterStatistics.entrySet()) {
				  statCategory.setValue(0);
			}
			
			//increment previous semester
			currentSemester++;
		}
		
		//TODO: update new semester back into DB
		
		//Load remaining files now that we know the semester
//		AssignmentsData.load(currentSemester);
//		RequestsData.load(currentSemester);
	}
	
	//This method will process all requests and assumes requests List already loaded
//	public static void processRequests() {
//		List<Request> requestsList = requests.get(currentSemester);
//		
//		for(Request r : requestsList) {
//			r.getStudent().enrollInCourse(r.getCourse());
//			
//			//log the status for each student request in debug mode
//			if (Log.isDebug()) {
//				Logger.debug(TAG, "Request from: " + r.getStudent().getFullName() + " is " + r.getStatus().name());
//			}
//		}
//		
//		//TODO: spruce up this output text if we would like it to differ from the assignment spec
//		System.out.println("Processed Requests");
//		for(Request r : requestsList) {
//			System.out.println("request (" + r.getStudent().getUUID() + ", " + r.getCourse().getID() + "): " + r.getReason());
//		}
//	}
	
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
		loadRequests();
		loadAssignments();

		if (Log.isDebug()) {
			Logger.debug(TAG, "designateSemester currentSemester: " + currentSemester);
		}
	}
	
	public static void loadAssignments() {
		// Load assignments for current semester
		List<Assignment> semAssignments = new ArrayList<>();
		assignments.put(currentSemester, semAssignments);
		AssignmentsData.loadFromCSV(currentSemester);
	}

	public static void loadRequests() {
		// Load requests for current semester
		List<Request> semRequests = new ArrayList<>();
		requests.put(currentSemester, semRequests);
		RequestsData.loadFromCSV(currentSemester);
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

	public static Map<Integer,List<Assignment>> getCapacities() {
		return capacities;
	}

	public static List<Assignment> getCapacities(Integer semester) {
		return capacities.get(semester);
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
	
	// UTILITY METHODS
	
	/**
	 * Called by UI, when instructors are chosen for a semester.
	 * @param selected
	 */
	public static void setChosenAssignments(List<String> selected){
		List<Assignment> semAssignments = new ArrayList<>();
		capacities.put(currentSemester, semAssignments);
		
		for (String element: selected) {
			String[] splits = element.split(",");
			String instructor = splits[0].substring("Instructor".length() + 1, splits[0].length());
			String course = splits[1].substring(" course".length() + 1, splits[1].length());
			String capac = splits[2].substring(" capacity".length() + 1, splits[2].length());
			Assignment assign = getAssignment(instructor, course, Integer.valueOf(capac));
			System.out.println("Found: " + assign);
			// add capacity, if there's already the course in the "chosen"
			boolean found = false;
			for (Assignment assign2: capacities.get(currentSemester)) {
				if (assign2.getCourse().getID() == assign.getCourse().getID()) {
					assign2.setCapacity(assign2.getCapacity() + assign.getCapacity());
					found = true;
					break;
				}
			}
			if (!found) capacities.get(currentSemester).add(assign);
			System.out.println(capacities.get(currentSemester));
		}
	}
	
	/**
	 * Method to find an assignment based on instructor, course and capacity (used with UI instructor selection).
	 * 
	 * @param instructor
	 * @param course
	 * @param capacity
	 * @return
	 */
	public static Assignment getAssignment(String instructor, String course, Integer capacity) {
		List<Assignment> semesterAssign = assignments.get(currentSemester);
		for (Assignment assign: semesterAssign) {
			if (instructor.equals(assign.getInstructor().getFullName())
					&& course.equals(assign.getCourse().getTitle())
					&& capacity == assign.getCapacity())
				return assign;
		}
		// something really wrong happened
		return null;
	}

	/**
	 * Requests processing, starts with the waiting list
	 */
	public static void processRequests() {
		// take care of waiting list first, the key is the courseId, and the values are the students, in order.
		List<Request> waiting = RequestsData.getWaiting();
		for (Request request: waiting) {
			processRequest(request);
		}
		
		// load and go through Requests for this semester
		List<Request> semRequests = requests.get(currentSemester);
		for(Request request: semRequests) {
			processRequest(request);
		}
	}

	/**
	 * Process one request. Check for conditions, if passed then create record. 
	 * @param request
	 */
	private static void processRequest(Request request) {
		Student student = request.getStudent();
		Course course = request.getCourse();
		
        if (! student.checkPrerequisites(course)) { // check missing prereqs
        	RequestsData.updateRequestDenied(RequestStatus.RejectedPrerequisites, student, course);
        }
        else if (! student.checkCourseRecords(course)) { // check previous records for the course
        	RequestsData.updateRequestDenied(RequestStatus.RejectedAlreadyTaken, student, course);
        }
        else if (! student.checkAvailableSeats(course)) { // check no seats
        	RequestsData.updateRequestDenied(RequestStatus.RejectedFullCapacity, student, course);
        }
        else {	// accepted!
        	RequestsData.updateRequestAccepted(RequestStatus.Accepted, student, course);

        	// reduce capacity for the course this semester
        	List<Assignment> assigns = capacities.get(currentSemester);
        	for (Assignment assign: assigns) {
        		if (assign.getCourse().getID() == course.getID())
        			assign.setCapacity(assign.getCapacity() - 1);
        	}
        	
        	// automatically create a Record for this student this course this semester
        	// find instructor (first one in list of assignments for this semester)
        	Instructor instructor = null;
        	assigns = assignments.get(currentSemester);
        	for (Assignment assign: assigns) {
        		if (assign.getCourse().getID() == course.getID())
        			instructor = assign.getInstructor();
        	}
        	// add the record to DB
        	Record record = new Record(student, course, instructor, "", GradeDistributionUtil.createRandomGrade());
        	RecordsData.save(record);
        	// add the record to model
        	records.add(record);
        }			
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
