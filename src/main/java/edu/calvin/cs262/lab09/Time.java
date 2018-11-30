package edu.calvin.cs262.lab09;

/**
 * This class implements a Player Data-Access Object (DAO) class for the Player relation.
 * This provides an object-oriented way to represent and manipulate player "objects" from
 * the traditional (non-object-oriented) Monopoly database.
 *
 */
public class Time {

    private int id;
    private String startTime, endTime;
    private int employeeID;
    private int projectID;
    private String myUUID;


    public Time() {
        // The JSON marshaller used by Endpoints requires this default constructor.
    }
    public Time(int id, String startTime, String endTime, int employeeID, int projectID, String myUUID) {
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

    public String getStartTime() {
        return this.startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public void setEndTime(String endTime) {
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
