package edu.calvin.cs262.lab09;

/**
 * This class implements a Player Data-Access Object (DAO) class for the Player relation.
 * This provides an object-oriented way to represent and manipulate player "objects" from
 * the traditional (non-object-oriented) Monopoly database.
 *
 */
public class Time {

    private int id;
    private java.sql.Timestamp startTime, endTime;
    private int employeeID;
    private int projectID;
    private String myUUID;


    public Time() {
        // The JSON marshaller used by Endpoints requires this default constructor.
    }
    public Time(int id, java.sql.Timestamp startTime, java.sql.Timestamp endTime, int employeeID, int projectID, String myUUID) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.employeeID = employeeID;
        this.projectID = projectID;
        this.myUUID = myUUID;
    }

    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public java.sql.Timestamp getStartTime() {
        return this.startTime;
    }

    public void setStartTime(java.sql.Timestamp startTime) {
        this.startTime = startTime;
    }

    public java.sql.Timestamp getEndTime() {
        return this.endTime;
    }

    public void setEndTime(java.sql.Timestamp endTime) {
        this.endTime = endTime;
    }

    public int getEmployeeID() {
        return this.employeeID;
    }
    public void setEmployeeID(int employeeID) {
        this.employeeID = employeeID;
    }

    public int getProjectID() {
        return this.projectID;
    }
    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public String getUUID() {
    	return this.myUUID;
    }

    public void setUUID(String myUUID) {
    	this.myUUID = myUUID;
    }

}
