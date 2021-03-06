package edu.calvin.cs262.project;

import com.google.api.server.spi.config.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static com.google.api.server.spi.config.ApiMethod.HttpMethod.GET;
import static com.google.api.server.spi.config.ApiMethod.HttpMethod.PUT;
import static com.google.api.server.spi.config.ApiMethod.HttpMethod.POST;
import static com.google.api.server.spi.config.ApiMethod.HttpMethod.DELETE;

/**
 * This Java annotation specifies the general configuration of the Google Cloud endpoint API.
 * The name and version are used in the URL: https://PROJECT_ID.appspot.com/monopoly/v1/ENDPOINT.
 * The namespace specifies the Java package in which to find the API implementation.
 * The issuers specifies boilerplate security features that we won't address in this course.
 *
 * You should configure the name and namespace appropriately.
 */
@Api(
        name = "teama",
        version = "v1",
        namespace =
        @ApiNamespace(
                ownerDomain = "project.cs262.calvin.edu",
                ownerName = "project.cs262.calvin.edu",
                packagePath = ""
        ),
        issuers = {
                @ApiIssuer(
                        name = "firebase",
                        issuer = "https://securetoken.google.com/calvincs262-fall2018-teama",
                        jwksUri =
                                "https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system"
                                        + ".gserviceaccount.com"
                )
        }
)

/**
 * This class implements a RESTful service for the Employee table of our project database.
 *
 * You can test the GET endpoints using a standard browser or cURL.
 *
 * % curl --request GET \
 *    https://calvincs262-fall2018-teama.appspot.com/monopoly/v1/employees
 *
 * % curl --request GET \
 *    https://calvincs262-fall2018-teama.appspot.com/monopoly/v1/employee/1
 *
 * You can test the full REST API using the following sequence of cURL commands (on Linux):
 * (Run get-employees between each command to see the results.)
 *
 * // Add a new employee (as a uniquely generated ID).
 * % curl --request POST \
 *    --header "Content-Type: application/json" \
 *    --data '{username":"test username...", "password":"test password..."}' \
 *    https://calvincs262-fall2018-teama.appspot.com/monopoly/v1/employee
 *
 * // Edit the new player (assuming ID #6).
 * % curl --request PUT \
 *    --header "Content-Type: application/json" \
 *    --data '{username":"test username...", "password":"test password..."}' \
 *    https://calvincs262-fall2018-teama.appspot.com/monopoly/v1/employee/6
 *
 * // Delete the new player (assuming ID #6).
 * % curl --request DELETE \
 *    https://calvincs262-fall2018-teama.appspot.com/monopoly/v1/employee/6
 *
 */
public class EmployeeResource {

    /**
     * GET
     * This method gets the full list of players from the Employee table.
     *
     * @return JSON-formatted list of employee records (based on a root JSON tag of "items")
     * @throws SQLException
     */
    @ApiMethod(path="employees", httpMethod=GET)
    public List<Employee> getEmployees() throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        List<Employee> result = new ArrayList<Employee>();
        try {
            connection = DriverManager.getConnection(System.getProperty("cloudsql"));
            statement = connection.createStatement();
            resultSet = selectEmployees(statement);
            while (resultSet.next()) {
                Employee emp = new Employee(
                        Integer.parseInt(resultSet.getString(1)),
                        resultSet.getString(2),
                        resultSet.getString(3)
                );
                result.add(emp);
            }
        } catch (SQLException e) {
            throw(e);
        } finally {
            if (resultSet != null) { resultSet.close(); }
            if (statement != null) { statement.close(); }
            if (connection != null) { connection.close(); }
        }
        return result;
    }

    /**
     * GET
     * This method gets the employee from the Employee table with the given ID.
     *
     * @param id the ID of the requested employee
     * @return if the employee exists, a JSON-formatted employee record, otherwise an invalid/empty JSON entity
     * @throws SQLException
     */
    @ApiMethod(path="employee/{id}", httpMethod=GET)
    public Employee getEmployee(@Named("id") int id) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        Employee result = null;
        try {
            connection = DriverManager.getConnection(System.getProperty("cloudsql"));
            statement = connection.createStatement();
            resultSet = selectEmployee(id, statement);
            if (resultSet.next()) {
                result = new Employee(
                        Integer.parseInt(resultSet.getString(1)),
                        resultSet.getString(2),
                        resultSet.getString(3)
                );
            }
        } catch (SQLException e) {
            throw(e);
        } finally {
            if (resultSet != null) { resultSet.close(); }
            if (statement != null) { statement.close(); }
            if (connection != null) { connection.close(); }
        }
        return result;
    }

    /**
     * PUT
     * This method creates/updates an instance of Employee with a given ID.
     * If the employee doesn't exist, create a new employee using the given field values.
     * If the employee already exists, update the fields using the new employee field values.
     * We do this because PUT is idempotent, meaning that running the same PUT several
     * times is the same as running it exactly once.
     * Any employee ID value set in the passed employee data is ignored.
     *
     * @param id     the ID for the employee, assumed to be unique
     * @param employee a JSON representation of the employee; The id parameter overrides any id specified here.
     * @return new/updated employee entity
     * @throws SQLException
     */
    @ApiMethod(path="employee/{id}", httpMethod=PUT)
    public Employee putEmployee(Employee employee, @Named("id") int id) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(System.getProperty("cloudsql"));
            statement = connection.createStatement();
            employee.setId(id);
            resultSet = selectEmployee(id, statement);
            if (resultSet.next()) {
                updateEmployee(employee, statement);
            } else {
                insertEmployee(employee, statement);
            }
        } catch (SQLException e) {
            throw (e);
        } finally {
            if (resultSet != null) { resultSet.close(); }
            if (statement != null) { statement.close(); }
            if (connection != null) { connection.close(); }
        }
        return employee;
    }

    /**
     * POST
     * This method creates an instance of Employee with a new, unique ID
     * number. We do this because POST is not idempotent, meaning that running
     * the same POST several times creates multiple objects with unique IDs but
     * otherwise having the same field values.
     *
     * The method creates a new, unique ID by querying the employee table for the
     * largest ID and adding 1 to that. Using a DB sequence would be a better solution.
     * This method creates an instance of Employee with a new, unique ID.
     *
     * @param employee a JSON representation of the employee to be created
     * @return new employee entity with a system-generated ID
     * @throws SQLException
     */
    @ApiMethod(path="employee", httpMethod=POST)
    public Employee postEmployee(Employee employee) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(System.getProperty("cloudsql"));
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT MAX(ID) FROM Employee");
            if (resultSet.next()) {
                employee.setId(resultSet.getInt(1) + 1);
            } else {
                throw new RuntimeException("failed to find unique ID...");
            }
            insertEmployee(employee, statement);
        } catch (SQLException e) {
            throw (e);
        } finally {
            if (resultSet != null) { resultSet.close(); }
            if (statement != null) { statement.close(); }
            if (connection != null) { connection.close(); }
        }
        return employee;
    }

    /**
     * DELETE
     * This method deletes the instance of Employee with a given ID, if it exists.
     * If the employee with the given ID doesn't exist, SQL won't delete anything.
     * This makes DELETE idempotent.
     *
     * @param id     the ID for the employee, assumed to be unique
     * @return the deleted employee, if any
     * @throws SQLException
     */
    @ApiMethod(path="employee/{id}", httpMethod=DELETE)
    public void deleteEmployee(@Named("id") int id) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DriverManager.getConnection(System.getProperty("cloudsql"));
            statement = connection.createStatement();
            deleteEmployee(id, statement);
        } catch (SQLException e) {
            throw (e);
        } finally {
            if (statement != null) { statement.close(); }
            if (connection != null) { connection.close(); }
        }
    }

    /** SQL Utility Functions *********************************************/

    /*
     * This function gets the employee with the given id using the given JDBC statement.
     */
    private ResultSet selectEmployee(int id, Statement statement) throws SQLException {
        return statement.executeQuery(
                String.format("SELECT * FROM Employee WHERE id=%d", id)
        );
    }

    /*
     * This function gets the employee with the given id using the given JDBC statement.
     */
    private ResultSet selectEmployees(Statement statement) throws SQLException {
        return statement.executeQuery(
                "SELECT * FROM Employee"
        );
    }

    /*
     * This function modifies the given employee using the given JDBC statement.
     */
    private void updateEmployee(Employee employee, Statement statement) throws SQLException {
        statement.executeUpdate(
                String.format("UPDATE Employee SET username='%s', password='%s' WHERE id=%d",
                        employee.getUsername(),
                        employee.getPassword(),
                        employee.getId()
                )
        );
    }

    /*
     * This function inserts the given employee using the given JDBC statement.
     */
    private void insertEmployee(Employee employee, Statement statement) throws SQLException {
        statement.executeUpdate(
                String.format("INSERT INTO Employee VALUES (%d, '%s', '%s')",
                        employee.getId(),
                        employee.getUsername(),
                        employee.getPassword()
                )
        );
    }

    /*
     * This function gets the employee with the given id using the given JDBC statement.
     */
    private void deleteEmployee(int id, Statement statement) throws SQLException {
        statement.executeUpdate(
                String.format("DELETE FROM Employee WHERE id=%d", id)
        );
    }

    /*
     * This function returns a value literal suitable for an SQL INSERT/UPDATE command.
     * If the value is NULL, it returns an unquoted NULL, otherwise it returns the quoted value.
     */
    private String getValueStringOrNull(String value) {
        if (value == null) {
            return "NULL";
        } else {
            return "'" + value + "'";
        }
    }

}
