package edu.sdccd.cisc191.template;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.Random;

/**
 * This class represents a bet placed on a game. It holds information about the game,
 * the team being bet on, the amount of the bet, potential winnings, and the odds of winning.
 * It also tracks the odds over time.
 *
 * The class can convert to and from JSON for saving or sharing bet information.
 *
 * @author Brian Tran, Andy Ly, Julian Garcia
 * @see Game
 * @see User
 */
public class Bet implements Serializable {

    private Game game;
    private String betTeam;
    private int betAmt;
    private int winAmt;
    private int winOdds;

    private final int numHours = 10; // Number of hours to track odds
    private final double[][] winOddsOvertime = new double[numHours][2]; // Array to track odds over time

    private boolean fulfillment;
    private final long currentEpochSeconds = System.currentTimeMillis() / 1000; // Current time in seconds

    @JsonIgnore
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Converts a Bet object into a JSON string.
     *
     * @param bet The Bet object to convert.
     * @return A JSON string representing the Bet.
     * @throws Exception If there is a problem converting to JSON.
     */
    public static String toJSON(Bet bet) throws Exception {
        // TODO: Check if the bet is null before trying to convert it to JSON.
        return objectMapper.writeValueAsString(bet);
    }

    /**
     * Converts a JSON string into a Bet object.
     *
     * @param input The JSON string to convert.
     * @return A Bet object created from the JSON string.
     * @throws Exception If there is a problem converting from JSON.
     */
    public static Bet fromJSON(String input) throws Exception {
        // TODO: Add a better error message if input is not a valid JSON.
        System.out.println(input);
        return objectMapper.readValue(input, Bet.class);
    }

    /**
     * Default constructor for Bet. Required for JSON conversion.
     */
    protected Bet() {
        // TODO: You could set some default values here if needed.
    }

    private final Random random = new Random();

    /**
     * Creates a new Bet with a game, bet amount, and team.
     * Initializes potential winnings, odds of winning, and tracks odds over time.
     *
     * @param g The game associated with the bet.
     * @param amt The amount of money being bet.
     * @param betTeam The team being bet on.
     */
    public Bet(Game g, int amt, String betTeam) {
        // TODO: Check if the bet amount is negative or if the game is null.
        this.game = g;
        this.betTeam = betTeam;
        this.betAmt = amt;

        if (betTeam.equalsIgnoreCase("team1")) {
            winOdds = (int) Game.getTeam1Odd();
        } else if (betTeam.equalsIgnoreCase("team2")) {
            winOdds = (int) Game.getTeam2Odd();

            if (winOdds >= 0) {
                this.winAmt = (amt + (100 / winOdds) * amt);
            } else {
                this.winAmt = (amt + Math.abs((winOdds / 100) * amt));
            }
        }

        // Fill winOddsOvertime with odds and timestamps
        for (int j = 0; j < numHours; j++) {
            long timeStamp = currentEpochSeconds - (j * 3600L); // Decrease by hours
            double odd = calculateOddsForGameAtTime(timeStamp);
            winOddsOvertime[j][0] = odd;
            winOddsOvertime[j][1] = timeStamp;
        }
    }



    /**
     * Calculates the odds for a game at a specific time.
     *
     * @param timeStamp The time for which to calculate the odds.
     * @return A random number representing the odds at that time.
     */
    private double calculateOddsForGameAtTime(long timeStamp) {
        // TODO: Instead of using random odds, think of a better way to calculate them.
        return 1 + random.nextInt(100); // Generate a random value between 1 and 100.
    }

    /**
     * Gets the potential winnings from the bet.
     *
     * @return The winning amount.
     */
    public int getWinAmt() {
        return winAmt;
    }

    /**
     * Sets the potential winnings for the bet.
     *
     * @param winAmt The winning amount to set.
     */
    public void setWinAmt(int winAmt) {
        // TODO: Make sure winAmt is a positive value.
        this.winAmt = winAmt;
    }

    /**
     * Gets the game associated with the bet.
     *
     * @return The associated game.
     */
    public Game getGame() {
        return game;
    }

    /**
     * Sets the game for the bet.
     *
     * @param game The game to set.
     */
    public void setGame(Game game) {
        // TODO: Add a check to ensure the game is not null.
        this.game = game;
    }

    /**
     * Gets the odds of winning the bet.
     *
     * @return The odds of winning as a percentage.
     */
    public double getWinOdds() {
        return winOdds;
    }

    /**
     * Gets the odds tracked over a 10-hour period.
     *
     * @return A 2D array with odds and timestamps.
     */
    public double[][] getWinOddsOvertime() {
        return winOddsOvertime;
    }

    /**
     * Updates the user's money based on whether they won the bet.
     *
     * @param user The user associated with the bet.
     * @return The updated user object.
     */
    public User updateUser(User user) {
        // TODO: Make sure the user object is not null before using it.
        if (fulfillment) {
            user.setMoney(user.getMoney() + winAmt); // Add winnings if bet is won.
        } else {
            user.setMoney(user.getMoney() - winAmt); // Subtract the bet amount if bet is lost.
        }
        return user;
    }

    /**
     * Updates whether the bet is fulfilled based on the odds of winning.
     */
    public void updateFulfillment() {
        // TODO: Improve how we calculate if the bet is won based on odds.
        int randomNumber = random.nextInt(100) + 1; // Generate a random number between 1 and 100.
        fulfillment = randomNumber <= winOdds; // If random number is less than or equal to odds, the bet is won.
    }

    /**
     * Gets the fulfillment status of the bet.
     *
     * @return True if the bet is fulfilled, otherwise false.
     */
    public boolean getFulfillment() {
        return this.fulfillment;
    }

    /**
     * Gets the team that is being bet on.
     *
     * @return The team being bet on.
     */
    public String getBetTeam() {
        return betTeam;
    }

    /**
     * Gets the amount of money placed on the bet.
     *
     * @return The bet amount.
     */
    public int getBetAmt() {
        return betAmt;
    }

    /**
     * Sets the amount of money placed on the bet.
     *
     * @param betAmt The bet amount to set.
     */
    public void setBetAmt(int betAmt) {
        // TODO: Make sure the bet amount is positive.
        this.betAmt = betAmt;
    }

    /**
     * Converts the Bet object into a string description.
     *
     * @return A string describing the bet.
     */
    @Override
    public String toString() {
        return "Bet on " + game + " for " + betAmt;
    }
}
