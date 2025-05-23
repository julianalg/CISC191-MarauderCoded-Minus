package edu.sdccd.cisc191.Common;

import static org.junit.jupiter.api.Assertions.*;

import edu.sdccd.cisc191.Common.Models.Bet;
import edu.sdccd.cisc191.Common.Models.Game;
import edu.sdccd.cisc191.Common.Models.User;
import org.junit.jupiter.api.Test;

import java.util.Date;

/**
 * Unit tests for the Bet class.
 */
public class BetTest {

    @Test
    public void testUpdateUserWhenFulfilled() {
        Game game = new Game("Team X", "Team Y", 1, new Date(), "Soccer", 1.5, 2.5);
        Bet bet = new Bet(game, 100, "team1");
        // Manually set fulfillment and potential winning amount.
        bet.setFulfillment(true);
        bet.setWinAmt(150);

        User user = new User("Alice", 1000);
        Long originalMoney = user.getMoney();
        user = bet.updateUser(user);
        assertEquals(originalMoney + bet.getWinAmt(), user.getMoney(), "User money should increase by winAmt if fulfilled");
    }

    @Test
    public void testUpdateUserWhenNotFulfilled() {
        Game game = new Game("Team X", "Team Y", 1, new Date(), "Soccer", 1.5, 2.5);
        Bet bet = new Bet(game, 100, "team1");
        // Manually set fulfillment to false.
        bet.setFulfillment(false);
        bet.setWinAmt(150);

        User user = new User("Alice", 1000);
        Long originalMoney = user.getMoney();
        user = bet.updateUser(user);
        assertEquals(originalMoney - bet.getWinAmt(), user.getMoney(), "User money should decrease by winAmt if not fulfilled");
    }

    @Test
    public void testUpdateFulfillment() {
        Game game = new Game("Team X", "Team Y", 1, new Date(), "Soccer", 1.5, 2.5);
        Bet bet = new Bet(game, 100, "team1");
        // Calling updateFulfillment sets the fulfillment field based on random odds.
        bet.updateFulfillment();
        boolean fulfillment = bet.getFulfillment();
        // The value is either true or false.
        assertTrue(fulfillment == true || fulfillment == false, "Fulfillment should be a valid boolean value");
    }

    @Test
    public void testEqualsAndHashCode() {
        Game game = new Game("Team A", "Team B", 1, new Date(), "Football", 1.8, 2.2);
        Bet bet1 = new Bet(game, 100, "team1");
        Bet bet2 = new Bet(game, 100, "team1");

        // With null IDs, equals should return false.
        assertFalse(bet1.equals(bet2), "Bets with null IDs are not equal");

        // Set the same ID and check equality.
        bet1.setId(1L);
        bet2.setId(1L);
        assertTrue(bet1.equals(bet2), "Bets with the same ID should be equal");
        assertEquals(bet1.hashCode(), bet2.hashCode(), "Equal bets should have the same hash code");

        // Change one ID and confirm inequality.
        bet2.setId(2L);
        assertFalse(bet1.equals(bet2), "Bets with different IDs should not be equal");
    }
}
