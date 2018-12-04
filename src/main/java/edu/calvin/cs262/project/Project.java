package edu.calvin.cs262.project;

/**
 * This class implements a Project Data-Access Object (DAO) class for the Project relation.
 * This provides an object-oriented way to represent and manipulate project "objects" from
 * our traditional project database.
 *
 */
public class Project {

    private int id;
    private String name;
    private int managerID;


    public Project() {
        // The JSON marshaller used by Endpoints requires this default constructor.
    }
    public Project(int id, String name, int managerID) {
        this.id = id;
        this.name = name;
        this.managerID = managerID;
    }

    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getManagerID() {
        return this.managerID;
    }

    public void setManagerID() {
        this.managerID = managerID;
    }

}
