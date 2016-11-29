package edu.gatech.cms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
import edu.gatech.cms.university.Department;
import edu.gatech.cms.university.Instructor;
import edu.gatech.cms.university.Student;
import edu.gatech.cms.university.University;
import edu.gatech.cms.util.DbHelper;

/**
 * The class takes care of most of data loading. 
 */
public class InputFileHandler {
	public static final String TAG = InputFileHandler.class.getSimpleName();

	private static final InputFileHandler instance = new InputFileHandler();

	private static University university;
	private static Department department;

	private static Map<Integer, Course> courses = new TreeMap<>();
	private static Map<Integer, Student> students = new TreeMap<>();
	private static Map<Integer, Instructor> instructors = new TreeMap<>();
	private static List<Record> records = new ArrayList<Record>();
	private static List<Request> requests;
	private static int currentSemester = 1;

    private WekaDataSource wekaDataSource = null;


    public static InputFileHandler getInstance() {
		return instance;
	}

	public void loadFromCSV() {
		if (Log.isDebug()) {
			Logger.debug(TAG, "loadFromCSV starts up");
		}

		DbHelper.dropTables();
		DbHelper.createTables();

        StudentsData.load();
		CoursesData.load();
        PrerequisitesData.load();
		InstructorsData.load();
		RecordsData.load();
	}

	public void designateSemester() {
		// Load requests and assignments for each semester using the currentSemester
		
		//extract the number out of the filename
		String fileName = "placeholder_1.csv";
		String semesterExtract = fileName.substring(fileName.indexOf("_")+1);
		System.out.println(semesterExtract);
		semesterExtract = semesterExtract.substring(0,semesterExtract.indexOf("."));
		System.out.println(semesterExtract);
		
		currentSemester = Integer.parseInt(semesterExtract);
		
		RequestsData.load(currentSemester);
		AssignmentsData.load(currentSemester);
	}

	public void prepareDataForDataMining() {
		if (wekaDataSource == null) {
			wekaDataSource = new WekaDataSource();
		}
	}

	public void analyzeHistoryAndRoster() {
		if (wekaDataSource == null) {
			if (Log.isDebug()) {
				Logger.debug(TAG, "Error: wekaDataSource is NULL!");
			}

			return;
		}

		// Test output only. 
		// TODO: Should be displayed in the UI
		System.out.println(wekaDataSource.analyzeStudentRecords());
		// System.out.println(wekaDataSource.analyzeCourseRequests());
	}

	public void calculateCapacityForCourse() {
		
	}

	public void lockAssignmentsForSemester() {
		
	}

	public void validateStudentRequests() {
		
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

    public static List<Request> getRequests() {
        return requests;
    }

    public static int getCurrentSemester() {
        return currentSemester;
    }

    public static void setCurrentSemester(int sem) {
        currentSemester = sem;
    }
}
