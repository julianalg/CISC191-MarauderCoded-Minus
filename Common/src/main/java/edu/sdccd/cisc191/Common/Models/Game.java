package edu.sdccd.cisc191.Common.Models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.joda.deser.DateTimeDeserializer;
import com.fasterxml.jackson.datatype.joda.ser.DateTimeSerializer;
import jakarta.persistence.*;
import lombok.*;
import org.joda.time.DateTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a game between two teams with details such as start and end dates,
 * betting odds, and a clean date string representation.
 * <p>
 * Supports JSON serialization and deserialization for easy integration
 * with external systems. Also includes methods for comparing game objects.
 *
 * @author Andy Ly, Julian Garcia
 */

@Configuration
class JacksonConfigGame {
    @Bean
    public JodaModule jodaModule() {
        return new JodaModule();
    }
}
@Entity
@Table(name = "games")
@Getter @Setter
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Game implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dbId;

    @Column(name = "api_id", unique = true)
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
    private double team1Odd;
    private double team2Odd;

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
     * Constructor for a Game.
     *
     * @param t1         Team 1 name
     * @param t2         Team 2 name
     * @param id         Internal game ID
     * @param givenDate  Date of the game
     * @param sport      Sport (e.g., "NFL")
     */
    public Game(String t1, String t2, long id, Date givenDate, String sport, long dbId) {
        this.team1 = t1;
        this.team2 = t2;
        this.id = id;
        this.gameDate = new DateTime(givenDate);
        this.sport = sport;
        this.dbId = dbId;

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
     * Default constructor for  Game .
     * Required for JSON serialization/deserialization.
     */
    public Game() {
        // Default constructor for deserialization purposes
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
     * Generates a clean string representation of the date range for the game.
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
}