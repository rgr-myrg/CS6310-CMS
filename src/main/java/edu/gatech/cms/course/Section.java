package edu.gatech.cms.course;

import java.util.ArrayList;
import java.util.List;

import edu.gatech.cms.university.Instructor;
import edu.gatech.cms.university.Student;

public class Section {

	private Course sectionOf;
	private Instructor taughtBy;
	private int sectionCapacity;
	private int numEnrolled;
	private String locationOffered;
	private List<Student> roster;
	private int semesterID;
	
	public Section(Course sectionOf, Instructor taughtBy, int sectionCapacity, int numEnrolled, String location) {
		this.sectionOf = sectionOf;
		this.taughtBy = taughtBy;
		this.sectionCapacity = sectionCapacity;
		this.numEnrolled = numEnrolled;
		locationOffered = location;
		roster = new ArrayList<Student>();
	}
	
	//GETTERS AND SETTERS SECTION
	public Course getSectionOf() {return sectionOf;}
	public void setSectionOf(Course sectionOf) {this.sectionOf = sectionOf;}
	public Instructor getTaughtBy() {return taughtBy;}
	public void setTaughtBy(Instructor taughtBy) {this.taughtBy = taughtBy;}
	public int getSectionCapacity() {return sectionCapacity;}
	public void setSectionCapacity(int capacity) {this.sectionCapacity = capacity;}
	public int getNumEnrolled() {return numEnrolled;}
	public void setNumEnrolled(int numEnrolled) {this.numEnrolled = numEnrolled;}
	public String getLocationOffered() {return locationOffered;}
	public void setLocationOffered(String locationOffered) {this.locationOffered = locationOffered;}
	public List<Student> getRoster() {return roster;}
	public void setRoster(List<Student> roster) {this.roster = roster;}
	public int getSemesterID() {return semesterID;}
	public void setSemesterID(int semesterID) {this.semesterID = semesterID;}
	//GETTERS AND SETTERS SECTION
	
	public void addStudentToSection(Student student) {
		roster.add(student);
		numEnrolled++;
		sectionOf.incrementNumEnrolled();
		
	}
	
	public boolean hasCapacity(){
		return (numEnrolled < sectionCapacity);
	}
	
	public void removeStudentFromSection(Student student) {
		roster.remove(student);
		numEnrolled--;
		sectionOf.minusOneNumEnrolled();
	}
	
}
