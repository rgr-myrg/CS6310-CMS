package edu.gatech.cms.course;

public enum RequestStatus {
	Pending,
	Accepted,
	RejectedPrerequisites,
	RejectedAlreadyTaken,
	RejectedFullCapacity;
	
	private final String[] messages = {
			"pending", 
			"valid",
			"student is missing one or more prerequisites",
			"student has already taken the course with a grade of C or higher",
			"no remaining seats at this time: (re-)added to waitlist"
	};
	
	public String getMessage() {
		return messages[ordinal()];
	}
}
