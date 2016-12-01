package edu.gatech.cms.course;

import edu.gatech.cms.university.Instructor;

public class Assignment {

	private Integer semester;
	private Instructor instructor;
	private Course course;
	private int capacity;

	public Assignment(Integer semester, Instructor instructor, Course course, int capacity) {
		this.semester = semester;
		this.instructor = instructor;
		this.course = course;
		this.capacity = capacity;
	}

	public Integer getSemester() {return semester;}
	public void setSemester(Integer semester) {this.semester = semester;}
	public Course getCourse() {return course;}
	public void setCourse(Course course) {this.course = course;}
	public Instructor getInstructor() {return instructor;}
	public void setInstructor(Instructor instructor) {this.instructor = instructor;}
	public int getCapacity() {return capacity;}
	public void setCapacity(int capacity) {this.capacity = capacity;}

	public String toString() {
		return "Assignment instructor id: " + instructor.getUUID() + 
				", course id: " + course.getID() + ", capacity: " + capacity;
	}
	
}
