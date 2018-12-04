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
 * This class implements a RESTful service for the project table of our project database.
 *
 * You can test the GET endpoints using a standard browser or cURL.
 *
 * % curl --request GET \
 *    https://calvincs262-fall2018-teama.appspot.com/monopoly/v1/projects
 *
 * % curl --request GET \
 *    https://calvincs262-fall2018-teama.appspot.com/monopoly/v1/project/1
 *
 * You can test the full REST API using the following sequence of cURL commands (on Linux):
 * (Run get-projects between each command to see the results.)
 *
 * // Add a new project (probably as unique generated ID #5).
 * % curl --request POST \
 *    --header "Content-Type: application/json" \
 *    --data '{"name":"test name...", "managerID":2}' \
 *    https://calvincs262-fall2018-teama.appspot.com/monopoly/v1/project
 *
 * // Edit the new project (assuming ID #5).
 * % curl --request PUT \
 *    --header "Content-Type: application/json" \
 *    --data '{"name":"new test name...", "managerID":2}' \
 *    https://calvincs262-fall2018-teama.appspot.com/monopoly/v1/project/5
 *
 * // Delete the new project (assuming ID #5).
 * % curl --request DELETE \
 *    https://calvincs262-fall2018-teama.appspot.com/monopoly/v1/project/5
 *
 */
public class ProjectResource {

    /**
     * GET
     * This method gets the full list of projects from the Project table.
     *
     * @return JSON-formatted list of project records (based on a root JSON tag of "items")
     * @throws SQLException
     */
    @ApiMethod(path="projects", httpMethod=GET)
    public List<Project> getProjects() throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        List<Project> result = new ArrayList<Project>();
        try {
            connection = DriverManager.getConnection(System.getProperty("cloudsql"));
            statement = connection.createStatement();
            resultSet = selectProjects(statement);
            while (resultSet.next()) {
                Project p = new Project(
                        Integer.parseInt(resultSet.getString(1)),
                        resultSet.getString(2),
                        Integer.parseInt(resultSet.getString(3))
                );
                result.add(p);
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
     * This method gets the project from the Project table with the given ID.
     *
     * @param id the ID of the requested project
     * @return if the project exists, a JSON-formatted project record, otherwise an invalid/empty JSON entity
     * @throws SQLException
     */
    @ApiMethod(path="project/{id}", httpMethod=GET)
    public Project getProject(@Named("id") int id) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        Project result = null;
        try {
            connection = DriverManager.getConnection(System.getProperty("cloudsql"));
            statement = connection.createStatement();
            resultSet = selectProject(id, statement);
            if (resultSet.next()) {
                result = new Project(
                        Integer.parseInt(resultSet.getString(1)),
                        resultSet.getString(2),
                        Integer.parseInt(resultSet.getString(3))
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
     * This method creates/updates an instance of Project with a given ID.
     * If the project doesn't exist, create a new project using the given field values.
     * If the project already exists, update the fields using the new project field values.
     * We do this because PUT is idempotent, meaning that running the same PUT several
     * times is the same as running it exactly once.
     * Any project ID value set in the passed project data is ignored.
     *
     * @param id     the ID for the project, assumed to be unique
     * @param project a JSON representation of the project; The id parameter overrides any id specified here.
     * @return new/updated project entity
     * @throws SQLException
     */
    @ApiMethod(path="project/{id}", httpMethod=PUT)
    public Project putProject(Project project, @Named("id") int id) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(System.getProperty("cloudsql"));
            statement = connection.createStatement();
            project.setId(id);
            resultSet = selectProject(id, statement);
            if (resultSet.next()) {
                updateProject(project, statement);
            } else {
                insertProject(project, statement);
            }
        } catch (SQLException e) {
            throw (e);
        } finally {
            if (resultSet != null) { resultSet.close(); }
            if (statement != null) { statement.close(); }
            if (connection != null) { connection.close(); }
        }
        return project;
    }

    /**
     * POST
     * This method creates an instance of Project with a new, unique ID
     * number. We do this because POST is not idempotent, meaning that running
     * the same POST several times creates multiple objects with unique IDs but
     * otherwise having the same field values.
     *
     * The method creates a new, unique ID by querying the project table for the
     * largest ID and adding 1 to that. Using a DB sequence would be a better solution.
     * This method creates an instance of Project with a new, unique ID.
     *
     * @param project a JSON representation of the project to be created
     * @return new project entity with a system-generated ID
     * @throws SQLException
     */
    @ApiMethod(path="project", httpMethod=POST)
    public Project postProject(Project project) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(System.getProperty("cloudsql"));
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT MAX(ID) FROM Project");
            if (resultSet.next()) {
                project.setId(resultSet.getInt(1) + 1);
            } else {
                throw new RuntimeException("failed to find unique ID...");
            }
            insertProject(project, statement);
        } catch (SQLException e) {
            throw (e);
        } finally {
            if (resultSet != null) { resultSet.close(); }
            if (statement != null) { statement.close(); }
            if (connection != null) { connection.close(); }
        }
        return project;
    }

    /**
     * DELETE
     * This method deletes the instance of Project with a given ID, if it exists.
     * If the project with the given ID doesn't exist, SQL won't delete anything.
     * This makes DELETE idempotent.
     *
     * @param id     the ID for the project, assumed to be unique
     * @return the deleted project, if any
     * @throws SQLException
     */
    @ApiMethod(path="project/{id}", httpMethod=DELETE)
    public void deleteProject(@Named("id") int id) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DriverManager.getConnection(System.getProperty("cloudsql"));
            statement = connection.createStatement();
            deleteProject(id, statement);
        } catch (SQLException e) {
            throw (e);
        } finally {
            if (statement != null) { statement.close(); }
            if (connection != null) { connection.close(); }
        }
    }

    /** SQL Utility Functions *********************************************/

    /*
     * This function gets the project with the given id using the given JDBC statement.
     */
    private ResultSet selectProject(int id, Statement statement) throws SQLException {
        return statement.executeQuery(
                String.format("SELECT * FROM Project WHERE id=%d", id)
        );
    }

    /*
     * This function gets the project with the given id using the given JDBC statement.
     */
    private ResultSet selectProjects(Statement statement) throws SQLException {
        return statement.executeQuery(
                "SELECT * FROM Project"
        );
    }

    /*
     * This function modifies the given project using the given JDBC statement.
     */
    private void updateProject(Project project, Statement statement) throws SQLException {
        statement.executeUpdate(
                String.format("UPDATE Project SET name='%s', managerID=%d WHERE id=%d",
                        project.getName(),
                        project.getManagerID(),
                        project.getId()
                )
        );
    }

    /*
     * This function inserts the given project using the given JDBC statement.
     */
    private void insertProject(Project project, Statement statement) throws SQLException {
        statement.executeUpdate(
                String.format("INSERT INTO Project VALUES (%d, '%s', %d)",
                        project.getId(),
                        project.getName(),
                        project.getManagerID()
                )
        );
    }

    /*
     * This function gets the project with the given id using the given JDBC statement.
     */
    private void deleteProject(int id, Statement statement) throws SQLException {
        statement.executeUpdate(
                String.format("DELETE FROM Project WHERE id=%d", id)
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
