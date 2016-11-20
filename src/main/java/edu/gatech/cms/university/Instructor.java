package edu.gatech.cms.university;

import java.util.ArrayList;
import java.util.List;

import edu.gatech.cms.course.Course;
import edu.gatech.cms.course.Section;

public class Instructor extends UniversityPerson {

	private List<Course> pastCoursesTaught;
	private List<Section> currentSectionsTaught;
	private boolean hasTaughtACourse;
	
	public Instructor(int UUID, String name, String address, String phone) {
		
		super.UUID =  UUID;
		super.fullName = name;
		super.primaryAddress = address;
		super.primaryPhone = phone;
		super.isSystemAdministrator = true;
		hasTaughtACourse = false;
		pastCoursesTaught = new ArrayList<Course>();
		currentSectionsTaught = new ArrayList<Section>();
	}
	
	public Instructor() {
		//empty constructor for InputFileHandler
	}
	
	public void setHasTaughtAClass(boolean hasTaughtACourse){
		this.hasTaughtACourse = hasTaughtACourse;
	}
	
	public boolean getHasTaughtACourse() {
		return hasTaughtACourse;
	}
	
	public void addPastCourseTaught(Course course) {
		pastCoursesTaught.add(course);
	}
	
	public void removePastCourseTaught(Course course) {
		pastCoursesTaught.remove(course);
	}
	
	public void addCurrentSectionTaught(Section section) {
		currentSectionsTaught.add(section);
	}
	
	public void removeCurrentSectionTaught(Section section) {
		currentSectionsTaught.remove(section);
	}
}
