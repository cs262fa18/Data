package edu.calvin.cs262.lab09;

/**
 * This class implements a Player Data-Access Object (DAO) class for the Player relation.
 * This provides an object-oriented way to represent and manipulate player "objects" from
 * the traditional (non-object-oriented) Monopoly database.
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
