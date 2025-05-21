package edu.sdccd.cisc191.Common.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.joda.deser.DateTimeDeserializer;
import com.fasterxml.jackson.datatype.joda.ser.DateTimeSerializer;
import jakarta.persistence.*;
import org.joda.time.DateTime;

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

import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class JacksonConfig {
    @Bean
    public JodaModule jodaModule() {
        return new JodaModule();
    }
}
@Entity
public class Game implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String team1;
    private String team2;
    private String sport;

    // Legendary 4 decorator field
    @Column(name = "game_date", length = 1024) // enough for the 279-byte blob
    @Lob                                       // makes it a BLOB in most DBs
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeserializer.class)
    private DateTime gameDate;

    private String dateClean;
    private static double team1Odd;
    private static double team2Odd;

    @JsonIgnore
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JodaModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


    /**
     * Constructor for a Game.
     *
     * @param t1         Team 1 name
     * @param t2         Team 2 name
     * @param id         Internal game ID
     * @param givenDate  Date of the game
     * @param sport      Sport (e.g., "NFL")
     * @param team1Odd   Decimal odds for team 1
     * @param team2Odd   Decimal odds for team 2
     */
    public Game(String t1, String t2, long id, Date givenDate, String sport, double team1Odd, double team2Odd) {
        this.team1 = t1;
        this.team2 = t2;
        this.id = id;
        this.gameDate = new DateTime(givenDate);
        this.sport = sport;


        this.team1Odd = team1Odd;
        this.team2Odd = team2Odd;
        this.dateClean = this.getDateClean();
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

    /**
     * Creates a  Game  object with betting odds loaded from an API.
     *
     * Does not calculate any odds.
     *
     * @param t1 The name of team 1.
     * @param t2 The name of team 2.
     * @param date The date of the game.
     * @param team1Odd The odds for team 1.
     * @param team2Odd The odds for team 2.
     */
    public Game(String t1, String t2, Date givenDate, String sport, double team1Odd, double team2Odd) {
        this.team1 = t1;
        this.team2 = t2;
        this.gameDate = new DateTime(givenDate);
        this.sport = sport;


        this.team1Odd = team1Odd;
        this.team2Odd = team2Odd;
        this.dateClean = this.getDateClean();
    }


    /**
     * Generates a string representation of the game.
     *
     * @return A string describing the game details.
     */
    @Override
    public String toString() {
        return team1 + " vs. " + team2 + " on " + gameDate.getMonthOfYear() + "/" + gameDate.getDayOfMonth() + "/" + gameDate.getYear();
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
        boolean startDateEquals = this.gameDate.compareTo(game.getGameDate()) == 0;
        boolean team1OddEquals = Math.abs(this.team1Odd - game.getTeam1Odd()) < 0.0001;
        boolean team2OddEquals = Math.abs(this.team2Odd - game.getTeam2Odd()) < 0.0001;

        return team1Equals && team2Equals && startDateEquals && team1OddEquals && team2OddEquals;
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
    public double getTeam1Odd() {
        return team1Odd;
    }

    public String getSport() { return sport; }

    /**
     * Gets the betting odds for team 2.
     *
     * @return The betting odds for team 2.
     */
    public double getTeam2Odd() {
        return team2Odd;
    }

    /**
     * Gets the start date of the game.
     *
     * @return The start date.
     */
    public DateTime getGameDate() {
        return this.gameDate;
    }

    /**
     * Generates a clean string representation of the date range for the game.
     *
     * Offsets are because of the way java.util.Date works.
     *
     * @return A string describing the start and end dates.
     */
    public String getDateClean() {
        if (gameDate.getMinuteOfHour() > 9) {
            return gameDate.getMonthOfYear() + "/" + gameDate.getDayOfMonth() + "/" + gameDate.getYear() + " " + gameDate.getHourOfDay() + ":" + gameDate.getMinuteOfHour();
        } else {
            return gameDate.getMonthOfYear() + "/" + gameDate.getDayOfMonth() + "/" + gameDate.getYear() + " " + gameDate.getHourOfDay() + ":0" + gameDate.getMinuteOfHour();
        }
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
        this.gameDate = new DateTime(startDate);
    }

    public long getId() {
        return this.id;
    }
}