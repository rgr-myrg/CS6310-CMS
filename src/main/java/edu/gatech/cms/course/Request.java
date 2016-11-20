package edu.gatech.cms.course;

import edu.gatech.cms.university.Student;

public class Request {

	private Student student;
	private Course course;
	private RequestStatus status;
	private String reason;
	
	public Request(Student student, Course course, RequestStatus status, String reason){
		this.student = student;
		this.course = course;
		this.status = status;
		this.reason = reason;
	}
	
	//GETTERS AND SETTERS SECTION
	public Student getStudent() {return student;}
	public void setStudent(Student student) {this.student = student;}
	public Course getCourse() {return course;}
	public void setCourse(Course course) {this.course = course;}
	public RequestStatus getStatus() {return status;}
	public void setStatus(RequestStatus status) {this.status = status;}
	public String getReason() {return reason;}
	public void setReason(String reason) {this.reason = reason;}
	//END GETTERS AND SETTERS SECTION
	
	
	
}
