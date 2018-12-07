package edu.calvin.cs262.project;

/**
 * This class implements an Employee Data-Access Object (DAO) class for the Employee relation.
 * This provides an object-oriented way to represent and manipulate employee "objects" from
 * our traditional project database.
 *
 */
public class Employee {

    private int id;
    private String username, password;


    public Employee() {
        // The JSON marshaller used by Endpoints requires this default constructor.
    }
    public Employee(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
