package edu.gatech.cms.university;

import edu.gatech.cms.course.Course;

public class Counselor extends UniversityPerson {

	public Counselor(int UUID, String name, String address, String phone) {
		super.UUID =  UUID;
		super.fullName = name;
		super.primaryAddress = address;
		super.primaryPhone = phone;
		super.isSystemAdministrator = true;
	}
	
	public void modifyCourseList(Course c, Student s, boolean addOrRemove) {
		if(addOrRemove == true) {
			//set to true means add the course to the student's course list
			s.addCourseToList(c);
		}
		else {
			//false means remove the course from the studen'ts course list
			s.removeCourseFromList(c);
		}
	}
}
