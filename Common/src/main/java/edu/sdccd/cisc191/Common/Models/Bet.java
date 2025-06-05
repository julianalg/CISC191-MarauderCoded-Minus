package edu.sdccd.cisc191.Common.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.Objects;
import java.util.Random;

/**
 * Represents a bet placed on a game. Contains information about the game,
 * the team being bet on, the bet amount, potential winnings, and the
 * odds of winning. Additionally, it tracks odds over time.
 *
 *  The class supports JSON serialization and deserialization for integration
 * with external systems and persistent storage.
 * The annotations autogenerate getters and setters for all fields.
 *
 * @author Brian Tran, Andy Ly, Julian Garcia
 * @see Game
 * @see User
 */
@Entity
@Table(name = "bets")          // if you want a specific table name
@Getter
@Setter
public class Bet implements Serializable {

    @ManyToOne
    @JoinColumn(name = "game_db_id", unique = true)
    // TODO: If multiple bets per game are allowed in the future, you can remove "unique = true"
    @Getter @Setter
    private Game game;
    private String betTeam;
    private int betAmt;
    private int winAmt;
    private int winOdds;

    private final int numHours = 10; // Number of hours to track odds

    private boolean fulfillment;
    private final long currentEpochSeconds = System.currentTimeMillis() / 1000; // Current time in seconds
    private final Random random = new Random();

    /**
     * -- GETTER --
     *  Gets the odds tracked over a 10-hour period.
     *
     * @return A 2D array representing odds and timestamps.
     */
    @Transient
    private final double[][] winOddsOvertime = new double[numHours][2]; // Array to track odds over time
    // TODO: Add a method to access or print this array for debugging or UI purposes

    @JsonIgnore
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Serializes a  Bet  object into a JSON string.
     *
     * @param bet The  Bet  object to serialize.
     * @return A JSON string representation of the  Bet .
     * @throws Exception If serialization fails.
     */
    public static String toJSON(Bet bet) throws Exception {
        return objectMapper.writeValueAsString(bet);
        // TODO: Add a null check for 'bet' to prevent errors if it's null
    }

    /**
     * Deserializes a JSON string into a  Bet  object.
     *
     * @param input The JSON string to deserialize.
     * @return A  Bet  object created from the JSON string.
     * @throws Exception If deserialization fails.
     */
    public static Bet fromJSON(String input) throws Exception {
        System.out.println(input);
        return objectMapper.readValue(input, Bet.class);
    }

    /**
     * Default constructor for  Bet .
     * Required for JSON serialization/deserialization.
     */
    protected Bet() {
        // Default constructor for deserialization purposes
    }


    /**
     * Constructs a new  Bet  with specified game, bet amount, and team.
     * Initializes potential winnings, odds of winning, and odds tracking over time.
     *
     * @param g The game associated with the bet.
     * @param amt The amount of money being bet.
     * @param betTeam The team being bet on.
     */
    public Bet(Game g, int amt, String betTeam, int winAmt) {
        this.game = g;
        this.betTeam = betTeam;
        this.betAmt = amt;
        this.winAmt = winAmt;
        // TODO: Consider checking if game, betTeam, and amounts are valid (not null or negative numbers)


        // Populate winOddsOvertime with odds and timestamps
        for (int j = 0; j < numHours; j++) {
            long timeStamp = currentEpochSeconds - (j * 3600L); // Decrement by hours
            double odd = calculateOddsForGameAtTime(timeStamp);
            winOddsOvertime[j][0] = odd;
            winOddsOvertime[j][1] = timeStamp;
        }

    }




    /**
     * Calculates the odds for a game at a specific timestamp.
     *
     * @param timeStamp The timestamp for which to calculate the odds.
     * @return A random value representing the odds at the specified time.
     */
    private double calculateOddsForGameAtTime(long timeStamp) {
        return 1 + random.nextInt(100); // Generate a random value between 1 and 100
        // TODO: In the future, if you want to, replace this with real logic instead of random numbers to make it more realistic
    }

    public double getWinOdds() {
        return winOdds;
    }

    /**
     * Updates the user's money based on the outcome of the bet.
     *
     * @param user The user associated with the bet.
     * @return The updated user object.
     */
    public User updateUser(User user) {
        if (fulfillment) {
            user.setMoney((int) (user.getMoney() + winAmt));
        } else {
            user.setMoney((int) (user.getMoney() - winAmt));
        }
        return user;
        // TODO: Consider adding a print or log to show the user's updated money for debugging purposes
    }

    /**
     * Updates the fulfillment status of the bet based on the odds of winning.
     */
    public void updateFulfillment() {
        int randomNumber = random.nextInt(100) + 1; // Generate a number from 1 to 100
        fulfillment = randomNumber <= winOdds;
    }

    /**
     * Gets the fulfillment status of the bet based on the odds of winning.
     */
    public boolean getFulfillment() {
        return this.fulfillment;
    }

    /**
     * Converts the  Bet  object into a string representation.
     *
     * @return A string describing the bet.
     */
    @Override
    public String toString() {
        return "Bet on " + game + " for " + betAmt;
        // TODO: You could include more info here like the team or win amount for better output
    }

    // IDE generated code to compare the bets
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Bet bet = (Bet) o;
        return getId() != null && Objects.equals(getId(), bet.getId());
    }


    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
