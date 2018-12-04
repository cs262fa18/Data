package edu.calvin.cs262.lab09;

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
        name = "monopoly",
        version = "v1",
        namespace =
        @ApiNamespace(
                ownerDomain = "lab09.cs262.calvin.edu",
                ownerName = "lab09.cs262.calvin.edu",
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
 * This class implements a RESTful service for the player table of the monopoly database.
 * Only the player table is supported, not the game or playergame tables.
 *
 * You can test the GET endpoints using a standard browser or cURL.
 *
 * % curl --request GET \
 *    https://calvincs262-monopoly.appspot.com/monopoly/v1/players
 *
 * % curl --request GET \
 *    https://calvincs262-monopoly.appspot.com/monopoly/v1/player/1
 *
 * You can test the full REST API using the following sequence of cURL commands (on Linux):
 * (Run get-players between each command to see the results.)
 *
 * // Add a new player (probably as unique generated ID #4).
 * % curl --request POST \
 *    --header "Content-Type: application/json" \
 *    --data '{"name":"test name...", "emailAddress":"test email..."}' \
 *    https://calvincs262-monopoly.appspot.com/monopoly/v1/player
 *
 * // Edit the new player (assuming ID #4).
 * % curl --request PUT \
 *    --header "Content-Type: application/json" \
 *    --data '{"name":"new test name...", "emailAddress":"new test email..."}' \
 *    https://calvincs262-monopoly.appspot.com/monopoly/v1/player/4
 *
 * // Delete the new player (assuming ID #4).
 * % curl --request DELETE \
 *    https://calvincs262-monopoly.appspot.com/monopoly/v1/player/4
 *
 */
public class TimeResource {

    /**
     * GET
     * This method gets the full list of players from the Player table.
     *
     * @return JSON-formatted list of player records (based on a root JSON tag of "items")
     * @throws SQLException
     */
    @ApiMethod(path="times", httpMethod=GET)
    public List<Time> getTimes() throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        List<Time> result = new ArrayList<Time>();
        try {
            connection = DriverManager.getConnection(System.getProperty("cloudsql"));
            statement = connection.createStatement();
            resultSet = selectTimes(statement);
            while (resultSet.next()) {
                Time t = new Time(
                        Integer.parseInt(resultSet.getString(1)),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        Integer.parseInt(resultSet.getString(5)),
                        Integer.parseInt(resultSet.getString(6))
                );
                result.add(t);
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
     * This method gets the player from the Player table with the given ID.
     *
     * @param id the ID of the requested player
     * @return if the player exists, a JSON-formatted player record, otherwise an invalid/empty JSON entity
     * @throws SQLException
     */
    @ApiMethod(path="time/{id}", httpMethod=GET)
    public Time getTime(@Named("id") int id) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        Time result = null;
        try {
            connection = DriverManager.getConnection(System.getProperty("cloudsql"));
            statement = connection.createStatement();
            resultSet = selectTime(id, statement);
            if (resultSet.next()) {
                result = new Time(
                        Integer.parseInt(resultSet.getString(1)),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        Integer.parseInt(resultSet.getString(5)),
                        Integer.parseInt(resultSet.getString(6))
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
     * This method creates/updates an instance of Person with a given ID.
     * If the player doesn't exist, create a new player using the given field values.
     * If the player already exists, update the fields using the new player field values.
     * We do this because PUT is idempotent, meaning that running the same PUT several
     * times is the same as running it exactly once.
     * Any player ID value set in the passed player data is ignored.
     *
     * @param id     the ID for the player, assumed to be unique
     * @param player a JSON representation of the player; The id parameter overrides any id specified here.
     * @return new/updated player entity
     * @throws SQLException
     */
    @ApiMethod(path="time/{id}", httpMethod=PUT)
    public Time putTime(Time time, @Named("id") int id) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(System.getProperty("cloudsql"));
            statement = connection.createStatement();
            time.setId(id);
            resultSet = selectTime(id, statement);
            if (resultSet.next()) {
                updateTime(time, statement);
            } else {
                insertTime(time, statement);
            }
        } catch (SQLException e) {
            throw (e);
        } finally {
            if (resultSet != null) { resultSet.close(); }
            if (statement != null) { statement.close(); }
            if (connection != null) { connection.close(); }
        }
        return time;
    }

    /**
     * POST
     * This method creates an instance of Person with a new, unique ID
     * number. We do this because POST is not idempotent, meaning that running
     * the same POST several times creates multiple objects with unique IDs but
     * otherwise having the same field values.
     *
     * The method creates a new, unique ID by querying the player table for the
     * largest ID and adding 1 to that. Using a DB sequence would be a better solution.
     * This method creates an instance of Person with a new, unique ID.
     *
     * @param player a JSON representation of the player to be created
     * @return new player entity with a system-generated ID
     * @throws SQLException
     */
    @ApiMethod(path="time", httpMethod=POST)
    public Time postTime(Time time) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(System.getProperty("cloudsql"));
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT MAX(ID) FROM Time");
            if (resultSet.next()) {
                time.setId(resultSet.getInt(1) + 1);
            } else {
                throw new RuntimeException("failed to find unique ID...");
            }
            insertTime(time, statement);
        } catch (SQLException e) {
            throw (e);
        } finally {
            if (resultSet != null) { resultSet.close(); }
            if (statement != null) { statement.close(); }
            if (connection != null) { connection.close(); }
        }
        return time;
    }

    /**
     * DELETE
     * This method deletes the instance of Person with a given ID, if it exists.
     * If the player with the given ID doesn't exist, SQL won't delete anything.
     * This makes DELETE idempotent.
     *
     * @param id     the ID for the player, assumed to be unique
     * @return the deleted player, if any
     * @throws SQLException
     */
    @ApiMethod(path="time/{id}", httpMethod=DELETE)
    public void deleteTime(@Named("id") int id) throws SQLException {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = DriverManager.getConnection(System.getProperty("cloudsql"));
            statement = connection.createStatement();
            deleteTime(id, statement);
        } catch (SQLException e) {
            throw (e);
        } finally {
            if (statement != null) { statement.close(); }
            if (connection != null) { connection.close(); }
        }
    }

    /** SQL Utility Functions *********************************************/

    /*
     * This function gets the player with the given id using the given JDBC statement.
     */
    private ResultSet selectTime(int id, Statement statement) throws SQLException {
        return statement.executeQuery(
                String.format("SELECT * FROM Time WHERE id=%d", id)
        );
    }

    /*
     * This function gets the player with the given id using the given JDBC statement.
     */
    private ResultSet selectTimes(Statement statement) throws SQLException {
        return statement.executeQuery(
                "SELECT * FROM Time"
        );
    }

    /*
     * This function modifies the given player using the given JDBC statement.
     */
    private void updateTime(Time time, Statement statement) throws SQLException {
        statement.executeUpdate(
                String.format("UPDATE Time SET uuid='%s, starttime='%s', endtime='%s', employeeid=%d, projectid=%d WHERE id=%d",
                        time.getUUID(),
                        time.getStartTime(),
                        time.getEndTime(),
                        time.getEmployeeID(),
                        time.getProjectID(),
                        time.getID()
                )
        );
    }

    /*
     * This function inserts the given player using the given JDBC statement.
     */
    private void insertTime(Time time, Statement statement) throws SQLException {
        statement.executeUpdate(
                String.format("INSERT INTO Time VALUES (%d, '%s', '%s', '%s', %d, %d)",
                        time.getID(),
                        time.getUUID(),
                        time.getStartTime(),
                        time.getEndTime(),
                        time.getEmployeeID(),
                        time.getProjectID()
                )
        );
    }

    /*
     * This function gets the player with the given id using the given JDBC statement.
     */
    private void deleteTime(int id, Statement statement) throws SQLException {
        statement.executeUpdate(
                String.format("DELETE FROM Time WHERE id=%d", id)
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
