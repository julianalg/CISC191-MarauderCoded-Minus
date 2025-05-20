package edu.sdccd.cisc191.Common.Models;

import java.io.Serializable;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.sdccd.cisc191.Common.Bet;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Represents a user in the system, holding information about their
 * name, available money, money currently in bets, and a list of active bets.
 * Provides methods to manage bets and serialize/deserialize the user object.
 *
 * The class is designed to integrate seamlessly with JSON-based systems,
 * enabling data exchange and persistent storage.
 *
 * @author Andy Ly, Julian Garcia
 */

// Adapted from Andrew Huang repo

@Entity
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int money;
    private int moneyLine; // Money placed in active bets but not yet resolved
    private int moneyBet; // Money available for future bets
    private ArrayList<Bet> bets = new ArrayList<>();

    @JsonIgnore
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Serializes a User object into a JSON string.
     *
     * @param customer The User object to serialize.
     * @return A JSON string representation of the  User .
     * @throws Exception If serialization fails.
     */
    public static String toJSON(User customer) throws Exception {
        return objectMapper.writeValueAsString(customer);
    }

    /**
     * Deserializes a JSON string into a  User  object.
     *
     * @param input The JSON string to deserialize.
     * @return A User object created from the JSON string.
     * @throws Exception If deserialization fails.
     */
    public static User fromJSON(String input) throws Exception {
        return objectMapper.readValue(input, User.class);
    }

    /**
     * Default constructor for  User .
     * Required for JSON serialization/deserialization.
     */
    protected User() {
        // Default constructor for deserialization purposes
    }

    /**
     * Creates a new User with the specified name and initial money.
     * Initializes moneyLine to 0 and  moneyBet  equal to the initial money.
     *
     * @param name The name of the user.
     * @param money The initial amount of money the user has.
     */
    public User(String name, int money) {
        this.name = name;
        this.money = money;
        this.moneyLine = 0;
        this.moneyBet = money;
    }

    /**
     * Checks if the user has an active bet on the specified game.
     *
     * @param game The game to check for active bets.
     * @return true if an active bet exists for the game, otherwise false.
     */
    public boolean checkBet(Game game) {
        for (Bet bet : bets) {
            boolean result = bet.getGame().equals(game);
            System.out.println("Checking bet: " + bet.getGame() + " with game: " + game + " Result: " + result);
            if (result) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves the amount of money currently in active bets.
     *
     * @return The amount of money in active bets.
     */
    public int getMoneyLine() {
        return moneyLine;
    }

    /**
     * Sets the amount of money currently in active bets.
     *
     * @param moneyLine The new amount of money in active bets.
     */
    public void setMoneyLine(int moneyLine) {
        this.moneyLine = moneyLine;
    }

    /**
     * Retrieves the amount of money available for future bets.
     *
     * @return The amount of money available for future bets.
     */
    public int getMoneyBet() {
        return moneyBet;
    }

    /**
     * Sets the amount of money available for future bets.
     *
     * @param moneyBet The new amount of money available for future bets.
     */
    public void setMoneyBet(int moneyBet) {
        this.moneyBet = moneyBet;
    }

    /**
     * Retrieves the user's name.
     *
     * @return The user's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's name.
     *
     * @param name The new name of the user.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the user's total money.
     *
     * @return The user's total money.
     */
    public int getMoney() {
        return money;
    }

    /**
     * Sets the user's total money.
     *
     * @param amt The amount of money to add to the user's balance.
     */
    public void setMoney(int amt) {
        this.money = amt;
    }

    /**
     * Increments the user's total money.
     *
     * @param amt The amount of money to add to the user's balance.
     */
    public void incrMoney(int amt) {
        this.money += amt;
    }

    /**
     * Decrements the user's total money.
     *
     * @param amt The amount of money to add to the user's balance.
     */
    public void decrMoney(int amt) {
        this.money -= amt;
    }
    /**
     * Retrieves the list of active bets for the user.
     *
     * @return A list of active bets.
     */
    public ArrayList<Bet> getBets() {
        return bets;
    }

    /**
     * Adds a new bet to the user's list of active bets and updates the money balance accordingly.
     *
     * @param b The bet to add.
     */
    public void addBet(Bet b) {
        bets.add(b);
        moneyBet -= b.getBetAmt();
        moneyLine += b.getBetAmt();
    }

    /**
     * Removes a bet from the user's list of active bets.
     *
     * @param b The bet to remove.
     */
    public void removeBet(Bet b) {
        bets.remove(b);
    }
}
