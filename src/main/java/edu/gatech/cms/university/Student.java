package edu.gatech.cms.university;

import java.util.ArrayList;
import java.util.List;

import edu.gatech.cms.InputFileHandler;
import edu.gatech.cms.course.Assignment;
import edu.gatech.cms.course.Course;
import edu.gatech.cms.course.Record;
import edu.gatech.cms.course.Request;
import edu.gatech.cms.course.RequestStatus;
import edu.gatech.cms.course.Section;
import edu.gatech.cms.util.GradeDistributionUtil;

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

    public boolean checkPrerequisites(Course course) {
        List<Course> prereqs = course.getPrerequisites();
        if (prereqs.isEmpty()) return true;
        boolean ok = true;
        
        for (Course prereq: prereqs) {
            boolean prereqOk = false;
            // check there is a record for that prereq
            for (Record record: recordHistory) {
                if (record.getCourse().getID() == prereq.getID())
                    if ("A".equals(record.getGradeEarned()) 
                            || "B".equals(record.getGradeEarned()) 
                            || "C".equals(record.getGradeEarned()) 
                            || "D".equals(record.getGradeEarned())) {
                        prereqOk = true;
                        continue;
                    }
            }
            ok = ok & prereqOk;
            if (!ok) return false;
        }
        
        return ok;
    }

    public boolean checkCourseRecords(Course course) {
        // it's possible that the student took several times that course
        for (Record record: recordHistory) {
            if (record.getCourse().getID() == course.getID())
                if ("A".equals(record.getGradeEarned()) 
                        || "B".equals(record.getGradeEarned()) 
                        || "C".equals(record.getGradeEarned())) {
                    return false;
                }
        }
        
        return true;
    }

    public boolean checkAvailableSeats(Course course) {
    	List<Assignment> chosen = InputFileHandler.getCapacities(InputFileHandler.getCurrentSemester());
    	for(Assignment assign: chosen) {
    		if (assign.getCourse().getID() == course.getID()) {
    			if (assign.getCapacity() > 0)
    				return true;
    		}
    	}
    	return false;
    }
	
	
	public String toString() {
	    return "Student id: " + UUID + ", name: " + fullName + ", address: " + primaryAddress + ", phone: " + primaryPhone;
	}
}
