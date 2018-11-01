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
            issuer = "https://securetoken.google.com/YOUR-PROJECT-ID",
            jwksUri =
                "https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system"
                    + ".gserviceaccount.com"
        )
    }
)

/**
 * This class implements a RESTful service for the player table of the monopoly database.
 * Only the player relation is supported, not the game or playergame relations.
 */
public class PlayerResource {

    /**
     * GET
     * This method gets the full list of players from the Player table. It uses JDBC to
     * establish a DB connection, construct/send a simple SQL query, and process the results.
     *
     * @return JSON-formatted list of player records (based on a root JSON tag of "items")
     * @throws SQLException
     */
    @ApiMethod(path="players", httpMethod=GET)
    public List<Player> getPlayers() throws SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        List<Player> result = new ArrayList<Player>();
        try {
            connection = DriverManager.getConnection(System.getProperty("cloudsql"));
            statement = connection.createStatement();
            resultSet = selectPlayers(statement);
            while (resultSet.next()) {
                Player p = new Player(
                        Integer.parseInt(resultSet.getString(1)),
                        resultSet.getString(2),
                        resultSet.getString(3)
                );
                result.add(p);
            }
        } catch (SQLException e) {
            throw(e);
        } finally {
            resultSet.close();
            statement.close();
            connection.close();
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
    @ApiMethod(path="player/{id}", httpMethod=GET)
    public Player getPlayer(@Named("id") int id) throws SQLException {
        // TODO: Implement the get-player resource (Lab 9).
        return null;
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
    @ApiMethod(path="player/{id}", httpMethod=PUT)
    public Player putPlayer(Player player, @Named("id") int id) throws SQLException {
        /*
         TODO: Implement/document the put-player resource (Homework 3).
         Algorithm (based on getPlayers():
             Setup JDBC connection, statement and resultSet.
             Connect to the cloudSQL DB.
             Try to select the player with the given ID.
             If it exists:
                 Update it using the given player field values
             Else:
                 Insert a new player using the given player values and ID.
             Handle exceptions and close resources.
             Return the player.
          */
        return player;
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
    @ApiMethod(path="player", httpMethod=POST)
    public Player postPlayer(Player player) throws SQLException {
        /*
        TODO: Implement/document the post-player resource (Homework 3).
         Algorithm (based on getPlayers():
             Setup JDBC connection, statement and resultSet.
             Connect to the cloudSQL DB.
             Select the largest player ID in the DB.
             Set the ID of the given player to the max(ID) + 1.
             Insert the new player into the DB.
             Handle exceptions and close resources.
             Return the player.
         */
        return player;
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
    @ApiMethod(path="player/{id}", httpMethod=DELETE)
    public void deletePlayer(@Named("id") int id) throws SQLException {
        /*
        TODO: Implement/document the delete-player resource (Homework 3).
         Algorithm (based on getPlayers():
             Setup JDBC connection and statement (no resultSet is needed).
             Connect to the cloudSQL DB.
             Delete the existing player with the given ID.
             Handle exceptions and close resources.
         */
    }

    /** SQL Utility Functions *********************************************/

    /*
     * This function gets the player with the given id using the given JDBC statement.
     */
    private ResultSet selectPlayers(Statement statement) throws SQLException {
        return statement.executeQuery(
                "SELECT * FROM Player"
        );
    }

    // TODO: Add utilities for selectPlayer(), updatePlayer(), insertPlayer() and deletePlayer().

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
