package edu.gatech.cms.university;

import java.util.List;

import edu.gatech.cms.course.Course;

public class Department {

	private String name;
	private List<Course> courseCatalog;
	
	public Department(String name, List<Course> cc) {
		this.name = name;
		courseCatalog = cc;
	}
	
	//GETTERS AND SETTERS SECTION
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public List<Course> getCourseCatalog() {return courseCatalog;}
	public void setCourseCatalog(List<Course> courseCatalog) {this.courseCatalog = courseCatalog;}
	//END GETTERS AND SETTERS SECTION
	
	public String viewCatalog() {
		//will populate this method based on customer requirements once they are available
		return "";
	}
	
	public void addCourseToCatalog(Course course){
		courseCatalog.add(course);
	}
	
	public void removeCourseFromCatalog(Course course){
		courseCatalog.remove(course);
	}
}
