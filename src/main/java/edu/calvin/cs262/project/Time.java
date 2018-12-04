package edu.calvin.cs262.project;

/**
 * This class implements a Time Data-Access Object (DAO) class for the Time relation.
 * This provides an object-oriented way to represent and manipulate time "objects" from
 * our traditional project database.
 *
 */
public class Time {

    private int id;
    private String myUUID;
    private String startTime, endTime;
    private int employeeID;
    private int projectID;


    public Time() {
        // The JSON marshaller used by Endpoints requires this default constructor.
    }
    public Time(int id, String myUUID, String startTime, String endTime, int employeeID, int projectID) {
        this.id = id;
        this.myUUID = myUUID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.employeeID = employeeID;
        this.projectID = projectID;
    }

    public int getID() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUUID() {
        return this.myUUID;
    }

    public void setUUID(String myUUID) {
        this.myUUID = myUUID;
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

}
