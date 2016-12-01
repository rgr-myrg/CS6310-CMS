package edu.gatech.cms.university;

import java.util.ArrayList;
import java.util.List;

import edu.gatech.cms.InputFileHandler;
import edu.gatech.cms.course.Course;
import edu.gatech.cms.course.Record;
import edu.gatech.cms.course.Request;
import edu.gatech.cms.course.RequestStatus;
import edu.gatech.cms.course.Section;

public class Student extends UniversityPerson {
    public static final int MAX_COURSES_ELIGIBLE = 5;

    private boolean isEnrolled;
	private boolean hasTakenCourse;
	private List<Section> sectionsCurrentlyEnrolled;
	private int coursesInCurrentSemester;
	private List<Course> approvedCourseList;
	private List<Request> requestsMade;
	private List<Record> recordHistory;
	
	public Student(int UUID, String name, String address, String phone) {
		
		super.UUID =  UUID;
		super.fullName = name;
		super.primaryAddress = address;
		super.primaryPhone = phone;
		super.isSystemAdministrator = false;

		hasTakenCourse = false;
		sectionsCurrentlyEnrolled = new ArrayList<Section>();
		approvedCourseList = new ArrayList<Course>();
		requestsMade = new ArrayList<Request>();
		recordHistory = new ArrayList<Record>();
	}
	
	public Student() {
	//empty constructor for InputFileHandler
	}
	
	//GETTERS AND SETTERS SECTION
	public boolean isEnrolled() {return isEnrolled;}
	public void setEnrolled(boolean isEnrolled) {this.isEnrolled = isEnrolled;}
	public boolean hasTakenCourse() {	return hasTakenCourse;}
	public void setHasTakenCourse(boolean hasTakenCourse) {this.hasTakenCourse = hasTakenCourse;}
	public List<Section> getSectionsCurrentlyEnrolled() {return sectionsCurrentlyEnrolled;}
	public void setSectionsCurrentlyEnrolled(List<Section> sectionsCurrentlyEnrolled) {this.sectionsCurrentlyEnrolled = sectionsCurrentlyEnrolled;}
	public int getCoursesInCurrentSemester() {return coursesInCurrentSemester;}
	public void setCoursesInCurrentSemester(int coursesInCurrentSemester) {this.coursesInCurrentSemester = coursesInCurrentSemester;}
	public List<Course> getApprovedCourseList() {return approvedCourseList;}
	public void setApprovedCourseList(List<Course> approvedCourseList) {this.approvedCourseList = approvedCourseList;}
	public List<Request> getRequestsMade() {return requestsMade;}
	public void setRequestsMade(List<Request> requestsMade) {this.requestsMade = requestsMade;}
	public List<Record> getRecordHistory() {return recordHistory;}
	public void setRecordHistory(List<Record> recordHistory) {this.recordHistory = recordHistory;}
	//END GETTERS AND SETTERS SECTION
	

	public boolean enrollInCourse(Course course) {
		//Status strings listed here for easy editing access
		String rejectedFullCapacity = "no remaining seats at this time: (re-)added to waitlist";
		String rejectedAlreadyTaken  = "student has already taken the course with a grade of C or higher";
		String rejectedPrerequisites = "student is missing one or more prerequisites";
		String accepted = "valid";
		
		InputFileHandler.incrementSemesterStats(InputFileHandler.getExaminedText());
		InputFileHandler.incrementTotalStats(InputFileHandler.getExaminedText());
		
		//check prereqs, 1st highest 'priority'
		if(hasPrerequisites(course)) {
			//check if eligible, 2nd highest 'priority'
			if(isEligibleToRetake(course)) {
				//check if course has capacity, 3rd highest 'priority'
				if(course.getTotalCourseCapacity() <= course.getTotalNumEnrolled()){
					addToCourseRequestHistory(course, rejectedFullCapacity);
					Request r = new Request(this, course, RequestStatus.RejectedFullCapacity,rejectedFullCapacity);
					InputFileHandler.addRequest(r);
					InputFileHandler.incrementSemesterStats(InputFileHandler.getFailedText());
					InputFileHandler.incrementTotalStats(InputFileHandler.getFailedText());
					return false;
				}
			}
			else {
				addToCourseRequestHistory(course, rejectedAlreadyTaken);
				Request r = new Request(this, course, RequestStatus.RejectedAlreadyTaken,rejectedAlreadyTaken);
				InputFileHandler.addRequest(r);
				InputFileHandler.incrementSemesterStats(InputFileHandler.getFailedText());
				InputFileHandler.incrementTotalStats(InputFileHandler.getFailedText());
				return false;
			}
		}
		else {
			addToCourseRequestHistory(course, rejectedPrerequisites);
			Request r = new Request(this, course, RequestStatus.RejectedPrerequisites,rejectedPrerequisites);
			InputFileHandler.addRequest(r);
			InputFileHandler.incrementSemesterStats(InputFileHandler.getFailedText());
			InputFileHandler.incrementTotalStats(InputFileHandler.getFailedText());
			return false;
		}
					
		//only arrive at this code if all 3 requirements met
		for( Section section : course.getSectionsOffered()){
			//Search for available sections, break when one is found
			if(enrollInSection(section)) {
				coursesInCurrentSemester++;
				//coursesGranted.add(course);
				//InputFileHandler.getInstance().incrementNumRequestsGranted();
				break;
			}
		}
		
		addToCourseRequestHistory(course, accepted);
		Request r = new Request(this, course, RequestStatus.Accepted,accepted);
		InputFileHandler.addRequest(r);
		InputFileHandler.incrementSemesterStats(InputFileHandler.getGrantedText());
		InputFileHandler.incrementTotalStats(InputFileHandler.getGrantedText());
		return true;
	}

	private boolean enrollInSection(Section section) {
		boolean enrollmentSuccessful = false;
		
		//all things that must be true before enrolling in a section
		boolean hasCapacity = section.hasCapacity();
		boolean eligibleForMoreSections = (coursesInCurrentSemester < MAX_COURSES_ELIGIBLE);
		
		if(hasCapacity && eligibleForMoreSections) {
			sectionsCurrentlyEnrolled.add(section);
			section.addStudentToSection(this);
			coursesInCurrentSemester++;
			enrollmentSuccessful = true;
		}
		else {
			//record the failed attempt to enroll in single section, currently no action needed 
		}
		
		return enrollmentSuccessful;
	}
	
	private boolean hasPrerequisites(Course course) {
		List<Course> prerequisites = course.getPrerequisites();
		//make sure all prerequisites were taken and passed
		for (Course c : prerequisites) { 
			if(!(hasTakenAndPassed(c))) {
				return false;
			}
		}
		return true;	
	}
	
	public void disenrollInSection(Section section) {
		sectionsCurrentlyEnrolled.remove(section);
		coursesInCurrentSemester--;
	}
	
	public void addCourseToList(Course course){
		approvedCourseList.add(course);
	}
	
	public void removeCourseFromList(Course course){
		approvedCourseList.remove(course);
	}
	
	private void addToCourseRequestHistory(Course course, String message){
		//TODO: create this method
		
	}
	
	public void addRecord(Record record){
		recordHistory.add(record);
	}
	
	public void removeRecord(Record record){
		recordHistory.remove(record);
	}
	
	private List<Record> recordsForCourse(Course course){
		List<Record> recordsForCourse = new ArrayList<Record>();
		for( Record record : recordHistory){
			if(record.getCourse().getID() == course.getID()) {
				recordsForCourse.add(record);
			}
		}
		return recordsForCourse;
	}
	
	public boolean hasTakenAndPassed(Course course) {
		boolean hasTakenAndPassed = false;
		List<Record> recordsForCourse = recordsForCourse(course);
		//check if course has ever been taken
		if(recordsForCourse.size() < 1) {
			return hasTakenAndPassed;
		}
		//check if course has ever been passed
		for( Record record : recordsForCourse) {
			if(!(record.getGradeEarned() == "F")) {
				hasTakenAndPassed = true;
				break;
			}
		}
		return hasTakenAndPassed;
	}
	
	public boolean isEligibleToRetake(Course course){
		boolean isEligible = false;
		List<Record> recordsForCourse = recordsForCourse(course);
		//check if course has ever been taken
		if(recordsForCourse.size() < 1) {
			isEligible = true;
		}
		else {
			//check if past course had a D or F
			for( Record record : recordsForCourse) {
				if((record.getGradeEarned() == "D") || (record.getGradeEarned() == "F")) {
					isEligible = true;
				} else {
					isEligible = false;
					break;
				}
			}
		}

		return isEligible;
	}

	public String toString() {
	    return "Student id: " + UUID + ", name: " + fullName + ", address: " + primaryAddress + ", phone: " + primaryPhone;
	}
}
