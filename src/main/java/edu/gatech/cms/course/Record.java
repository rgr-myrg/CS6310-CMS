package edu.gatech.cms.course;

import edu.gatech.cms.university.Instructor;
import edu.gatech.cms.university.Student;

public class Record {
	
	private Student student;
	private Course course;
	private Instructor instructor;
	private String instructorComments;
	private String gradeEarned;
	private int semesterTakenID;
	private boolean passedCourse;
	private boolean eligibleToRetake;
	
	public Record(Student student, Course course, Instructor instructor, String comments, String grade) {
		this.student = student;
		this.course = course;
		this.instructor = instructor;
		instructorComments = comments;
		gradeEarned = grade;
		this.course.setHasBeenTaken(true);
		this.instructor.setHasTaughtAClass(true);
		this.student.setHasTakenCourse(true);
		//TODO add logic for passedCourse and eligibleToRetake
	}
	
	public Record() {
		//blank constructor for RecordHistory method
	}

	//GETTERS AND SETTERS SECTION
	public Student getStudent() {return student;}
	public void setStudent(Student student) {this.student = student;}
	public Course getCourse() {return course;}
	public void setCourse(Course course) {this.course = course;}
	public Instructor getInstructor() {return instructor;}
	public void setInstructor(Instructor instructor) {this.instructor = instructor;}
	public String getInstructorComments() {return instructorComments;}
	public void setInstructorComments(String instructorComments) {this.instructorComments = instructorComments;}
	public String getGradeEarned() {return gradeEarned;}
	public void setGradeEarned(String gradeEarned) {this.gradeEarned = gradeEarned;}
	public int getSemesterTakenID() {return semesterTakenID;}
	public void setSemesterTakenID(int semesterTakenID) {this.semesterTakenID = semesterTakenID;}
	//END GETTERS AND SETTERS SECTION
	
}
