package edu.gatech.cms;

import java.util.ArrayList;
import java.util.List;

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

public class InputFileHandler {
	public static final String TAG = InputFileHandler.class.getSimpleName();

	private static final InputFileHandler instance = new InputFileHandler();

	private University university;
	private Department department;

	private List<Course> courses = new ArrayList<Course>();
	private List<Student> students = new ArrayList<Student>();
	private List<Instructor> instructors = new ArrayList<Instructor>();
	private List<Record> records = new ArrayList<Record>();
	private List<Request> requests;

	private WekaDataSource wekaDataSource = null;

	private int currentSemester = 0;

	public static InputFileHandler getInstance() {
		return instance;
	}

	public void loadFromCSV() {
		if (Log.isDebug()) {
			Logger.debug(TAG, "loadFromCSV starts up");
		}

		DbHelper.dropTables();
		DbHelper.createTables();

		CoursesData.load();
		InstructorsData.load();
		RecordsData.load();
		StudentsData.load();
		PrerequisitesData.load();
	}

	public void designateSemester() {
		// Load requests and assignments for each semester using the currentSemester
		RequestsData.load(currentSemester);
		AssignmentsData.load(currentSemester);
	}

	public void prepareDataForDataMining() {
		if (wekaDataSource == null) {
			wekaDataSource = new WekaDataSource();
		}
	}

	public String analyzeHistoryAndRoster() {
		if (wekaDataSource == null) {
			if (Log.isDebug()) {
				Logger.debug(TAG, "Error: wekaDataSource is NULL!");
			}

			return null;
		}

		// Test output only. 
		// TODO: Should be displayed in the UI
		//System.out.println(wekaDataSource.analyzeStudentRecords());
		return "TEST OUTPUT";
	}

	public void calculateCapacityForCourser() {
		
	}

	public void loackAssignmentsForSemester() {
		
	}

	public void validateStudentRequests() {
		
	}

	public int getCurrentSemester() {
		return currentSemester;
	}
}
