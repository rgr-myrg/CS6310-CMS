package edu.gatech.cms.university;

/**
 * Person, the parent class for Student, Instructor, Counselor.
 */
public abstract class UniversityPerson {
	
	protected int UUID;
	protected String fullName;
	protected String primaryAddress;
	protected String primaryPhone;
	protected boolean isSystemAdministrator;
	
	//GETTER AND SETTER SECTION
	public int getUUID(){return UUID;}
    public void setUUID(int uUID) {UUID = uUID;}
	public String getFullName() {return fullName;}
	public void setFullName(String fullName) {this.fullName = fullName;}
    public String getPrimaryAddress() {return primaryAddress;}
    public void setPrimaryAddress(String primaryAddress) {this.primaryAddress = primaryAddress;}
    public String getPrimaryPhone() {return primaryPhone;}
    public void setPrimaryPhone(String primaryPhone) {this.primaryPhone = primaryPhone;}
    public boolean isSystemAdministrator() {return isSystemAdministrator;}
    public void setSystemAdministrator(boolean isSystemAdministrator) {this.isSystemAdministrator = isSystemAdministrator;}
	
}
