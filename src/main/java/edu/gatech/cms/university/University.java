package edu.gatech.cms.university;

import java.util.List;

public class University {

	private String name;
	private List<Department> departments;
	
	public University(String name, List<Department> departments){
		this.name = name;
		this.departments = departments;
	}
	
	//GETTERS AND SETTERS SECTION
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public List<Department> getDepartments() {return departments;}
	public void setDepartments(List<Department> departments) {this.departments = departments;}
	//END GETTERS AND SETTERS SECTION
	
	public void addDepartment(Department d) {
		departments.add(d);
	}
	
	public void removeDepartment(Department d) {
		departments.remove(d);
	}
}
