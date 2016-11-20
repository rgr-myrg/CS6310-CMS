package edu.gatech.cms.university;

public abstract class UniversityPerson {
	
	protected int UUID;
	protected String fullName;
	protected String primaryAddress;
	protected String primaryPhone;
	protected boolean isSystemAdministrator;
	
	//GETTER AND SETTER SECTION
	public int getUUID(){return UUID;}
	public String getFullName() {return fullName;}
	public void setFullName(String fullName) {this.fullName = fullName;}
	public void setUUID(int uUID) {UUID = uUID;}
	
	
}
