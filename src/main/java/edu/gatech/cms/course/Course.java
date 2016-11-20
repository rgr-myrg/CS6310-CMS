package edu.gatech.cms.course;

import java.util.ArrayList;
import java.util.List;

import edu.gatech.cms.university.Student;

public class Course {
	
	private int courseID;
	private String title;
	private List<Section> sectionsOffered;
	private List<Course> prerequisites;
	private String description;
	private List<Integer> semestersOffered;
	private List<Student> waitlist;
	private boolean hasBeenTaken;
	private int totalCourseCapacity;
	private int totalNumEnrolled;
	
	public Course(int ID, String title, String desc) {
		
		courseID = ID;
		this.title = title;
		description = desc;
		hasBeenTaken = false;
		sectionsOffered = new ArrayList<Section>();
		prerequisites = new ArrayList<Course>();
		waitlist = new ArrayList<Student>();
		semestersOffered = new ArrayList<Integer>();
	}
	
	public Course() {
		//empty constructor for InputFileHandler
	}
	
	//GETTERS AND SETTERS SECTION
	public int getID() {return courseID;}
	public void setCourseID(int courseID) {this.courseID = courseID;}
	public String getTitle() {return title;}
	public void setTitle(String title) {this.title = title;}
	public List<Section> getSectionsOffered() {return sectionsOffered;}
	public void setSectionsOffered(List<Section> sectionsOffered) {this.sectionsOffered = sectionsOffered;}
	public String getDescription() {return description;}
	public void setDescription(String description) {this.description = description;}
	public List<Integer> getSemestersOffered() {return semestersOffered;}
	public void setSemestersOffered(List<Integer> semestersOffered) {this.semestersOffered = semestersOffered;}
	public void setHasBeenTaken(boolean hasBeenTaken){this.hasBeenTaken = hasBeenTaken;}
	public boolean getHasBeenTaken() {return hasBeenTaken;}
	public int getTotalCourseCapacity() {return totalCourseCapacity;}
	public void setTotalCourseCapacity(int totalCourseCapacity) {this.totalCourseCapacity = totalCourseCapacity;}
	public int getTotalNumEnrolled() {return totalNumEnrolled;}
	public void setTotalNumEnrolled(int totalNumEnrolled) {this.totalNumEnrolled = totalNumEnrolled;}
	public List<Course> getPrerequisites() {return prerequisites;}
	public void setPrerequisites(List<Course> prerequisites) {this.prerequisites = prerequisites;}
	public void incrementNumEnrolled() {totalNumEnrolled++;}
	public void minusOneNumEnrolled() {totalNumEnrolled--;}
	public List<Student> getWaitlist() {return waitlist;}
	public void setWaitlist(List<Student> waitlist) {this.waitlist = waitlist;}
	//END GETTERS AND SETTERS SECTION
	
	public void addSection(Section section){
		sectionsOffered.add(section);
		totalCourseCapacity += section.getSectionCapacity();
	}
	
	public void removeSection(Section section){
		sectionsOffered.remove(section);
		totalCourseCapacity -= section.getSectionCapacity();
	}
	
	public void addPrerequisite(Course course){
		prerequisites.add(course);
	}
	
	public void removePrerequisite(Course course){
		prerequisites.remove(course);
	}
	
	public void addStudentToWaitlist(Student student) {
		waitlist.add(student);
	}
	
	public void removeStudentFromWaitlist(Student student) {
		waitlist.remove(student);
	}
	
}
