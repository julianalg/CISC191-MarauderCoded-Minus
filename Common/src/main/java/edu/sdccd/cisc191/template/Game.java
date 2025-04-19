package edu.sdccd.cisc191.template;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Represents a game between two teams with details such as start and end dates,
 * betting odds, and a clean date string representation.
 *
 * Supports JSON serialization and deserialization for easy integration
 * with external systems. Also includes methods for comparing game objects.
 *
 * @author Andy Ly, Julian Garcia
 */

public class Game implements Serializable {

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Exclude from serialization
    private String sport;
    private String team1;
    private String team2;
    private Date startDate;
    private Date endDate;
    private String dateClean;
    private static double team1Odd;
    private static double team2Odd;
    public double team1Wager;
    public double team2Wager;
    public double betPool;
    public double team1PayoutRatio;
    public double team2PayoutRatio;
    public double team1ProfitFactor;
    public double team2ProfitFactor;


    @JsonIgnore
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public Game(String awayTeamName, String homeTeamName) {
    }

    /**
     * Serializes a  Game  object into a JSON string.
     *
     * @param customer The  Game  object to serialize.
     * @return A JSON string representation of the  Game .
     * @throws Exception If serialization fails.
     */
    public static String toJSON(Game customer) throws Exception {
        return objectMapper.writeValueAsString(customer);
    }

    /**
     * Deserializes a JSON string into a  Game  object.
     *
     * @param input The JSON string to deserialize.
     * @return A  Game  object created from the JSON string.
     * @throws Exception If deserialization fails.
     */
    public static Game fromJSON(String input) throws Exception {
        return objectMapper.readValue(input, Game.class);
    }

    /**
     * Default constructor for  Game .
     * Required for JSON serialization/deserialization.
     */
    protected Game() {
        // Default constructor for deserialization purposes
    }

    public String getGameType() {return "a";}


    /**
     * Creates a  Game  object with mock betting odds.
     *
     * @param t1 The name of team 1.
     * @param t2 The name of team 2.
     * @param startDate The start date of the game.
     * @param endDate The end date of the game.
     */
    public Game(String t1, String t2, Date startDate, Date endDate) {
        this.team1 = t1;
        this.team2 = t2;
        this.startDate = startDate;
        this.endDate = endDate;

        this.team1Odd = Math.round(Math.random() * 100);
        this.team2Odd = Math.round(Math.random() * 100);
        this.dateClean = this.getDateClean();
    }
    /**
     * Creates a  Game  object with betting odds loaded from an API.
     *
     * Does not calculate any odds.
     *
     * @param t1 The name of team 1.
     * @param t2 The name of team 2.
     * @param startDate The start date of the game.
     * @param endDate The end date of the game.
     * @param team1Odd The odds for team 1.
     * @param team2Odd The odds for team 2.
     */
    public Game(String t1, String t2, Date startDate, Date endDate, double team1Odd, double team2Odd) {
        this.team1 = t1;
        this.team2 = t2;
        this.startDate = startDate;
        this.endDate = endDate;

        this.team1Odd = team1Odd;
        this.team2Odd = team2Odd;
        this.dateClean = this.getDateClean();
    }

    /**
     * Creates a  Game  object with betting odds loaded from an API.
     *
     * @param t1 The name of team 1.
     * @param t2 The name of team 2.
     * @param startDate The start date of the game.
     * @param endDate The end date of the game.
     * @param team1Odd The odds for team 1.
     * @param team2Odd The odds for team 2.
     */
    public Game(String t1, String t2, Date startDate, Date endDate, double team1Odd, double team2Odd, double pool) {
        this.team1Wager = 80;
        this.team2Wager = 20;
        this.betPool = team1Wager + team2Wager;

        this.team1PayoutRatio = betPool / team1Wager;
        this.team2PayoutRatio = betPool / team2Wager;

        this.team1ProfitFactor = team1PayoutRatio - 1;
        this.team2ProfitFactor = team2PayoutRatio - 1;

        if (team1ProfitFactor >= 1) {
            this.team1Odd = +(team1ProfitFactor * 100);
        }
        else {
            this.team1Odd = -(100/team1ProfitFactor);
        }
        if (this.team2ProfitFactor >= 1) {
            this.team2Odd = +(team2ProfitFactor * 100);
        }
        else {
            this.team2Odd = -(100/team2ProfitFactor);
        }
    }

    /**
     * Generates a string representation of the game.
     *
     * @return A string describing the game details.
     */
    @Override
    public String toString() {
//        return team1 + " vs. " + team2 + " on " + startDate.getMonth() + "/" + startDate.getDate() + "/" + (startDate.getYear() + 1900);
        return team1 + " vs. " + team2;
    }

    /**
     * Compares two  Game  objects for equality based on teams,
     * dates, and odds.
     *
     * @param obj The other object to compare with.
     * @return  true  if the two objects are equal, otherwise  false .
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Game game = (Game) obj;

        boolean team1Equals = Objects.equals(this.team1, game.getTeam1());
        boolean team2Equals = Objects.equals(this.team2, game.getTeam2());
        boolean startDateEquals = this.startDate.compareTo(game.getStartDate()) == 0;
        boolean endDateEquals = this.endDate.compareTo(game.getEndDate()) == 0;
        boolean team1OddEquals = Math.abs(this.team1Odd - game.getTeam1Odd()) < 0.0001;
        boolean team2OddEquals = Math.abs(this.team2Odd - game.getTeam2Odd()) < 0.0001;

        return team1Equals && team2Equals && startDateEquals && endDateEquals && team1OddEquals && team2OddEquals;
    }

    /**
     * Gets the name of team 1.
     *
     * @return The name of team 1.
     */
    public String getTeam1() {
        return team1;
    }

    /**
     * Gets the name of team 2.
     *
     * @return The name of team 2.
     */
    public String getTeam2() {
        return team2;
    }

    /**
     * Gets the betting odds for team 1.
     *
     * @return The betting odds for team 1.
     */
    public static double getTeam1Odd() {
        return team1Odd;
    }

    /**
     * Gets the betting odds for team 2.
     *
     * @return The betting odds for team 2.
     */
    public static double getTeam2Odd() {
        return team2Odd;
    }

    /**
     * Gets the start date of the game.
     *
     * @return The start date.
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Gets the end date of the game.
     *
     * @return The end date.
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Generates a clean string representation of the date range for the game.
     *
     * Offsets are because of the way java.util.Date works.
     *
     * @return A string describing the start and end dates.
     */
    public String getDateClean() {
        return (startDate.getMonth() + 1) + "/" + startDate.getDate() + "/" + (startDate.getYear() + 1900) + " - " +
                (endDate.getMonth() + 1) + "/" + endDate.getDate() + "/" + (endDate.getYear() + 1900);
    }

    /**
     * Sets the name of team 1.
     *
     * @param team1 The new name for team 1.
     */
    public void setTeam1(String team1) {
        this.team1 = team1;
    }

    /**
     * Sets the name of team 2.
     *
     * @param team2 The new name for team 2.
     */
    public void setTeam2(String team2) {
        this.team2 = team2;
    }

    /**
     * Sets the start date of the game.
     *
     * @param startDate The new start date.
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Sets the end date of the game.
     *
     * @param endDate The new end date.
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
