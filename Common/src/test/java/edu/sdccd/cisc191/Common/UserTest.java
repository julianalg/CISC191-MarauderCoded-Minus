package edu.sdccd.cisc191.Common.Models;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.Date;

/**
 * Unit tests for the User class.
 */
public class UserTest {

    @Test
    public void testMoneyUpdates() {
        User user = new User("Bob", 500);
        user.incrMoney(200);
        assertEquals(700, user.getMoney(), "Money should be incremented by 200");
        user.decrMoney(300);
        assertEquals(400, user.getMoney(), "Money should be decremented by 300");
    }

    @Test
    public void testAddRemoveBet() {
        User user = new User("Charlie", 1000);
        // Create a game that will be associated with the bet
        Game game = new Game("Team A", "Team B", 1, new Date(), "Soccer", 1.5, 2.5);
        Bet bet = new Bet(game, 200, "team1");

        int initialMoneyBet = user.getMoneyBet();
        int initialMoneyLine = user.getMoneyLine();

        user.addBet(bet);
        // Verify the bet has been added.
        assertTrue(user.getBets().contains(bet), "Bets should contain the added bet");
        // Money bet decreases and money line increases by the bet amount.
        assertEquals(initialMoneyBet - bet.getBetAmt(), user.getMoneyBet(), "moneyBet should decrease by bet amount");
        assertEquals(initialMoneyLine + bet.getBetAmt(), user.getMoneyLine(), "moneyLine should increase by bet amount");

        // Remove the bet and ensure it no longer appears.
        user.removeBet(bet);
        assertFalse(user.getBets().contains(bet), "Bets should not contain the removed bet");
    }

    @Test
    public void testCheckBet() {
        User user = new User("Dana", 1000);
        Game game1 = new Game("Team A", "Team B", 1, new Date(), "Basketball", 1.8, 2.2);
        Game game2 = new Game("Team C", "Team D", 2, new Date(), "Basketball", 1.9, 2.1);
        Bet bet = new Bet(game1, 150, "team1");

        // Before the bet is added, checkBet should be false.
        assertFalse(user.checkBet(game1), "User should not have an active bet for team games yet");

        // Add bet and then verify.
        user.addBet(bet);
        assertTrue(user.checkBet(game1), "User should have an active bet for game1");
        assertFalse(user.checkBet(game2), "User should not have an active bet for game2");
    }

    @Test
    public void testEqualsAndHashCode() {
        User user1 = new User("Eve", 1000);
        User user2 = new User("Eve", 1000);
        // With null IDs, equals should return false.
        assertFalse(user1.equals(user2), "Users with null IDs should not be equal");

        // Set the same ID and check equality.
        user1.setId(1L);
        user2.setId(1L);
        assertTrue(user1.equals(user2), "Users with the same ID should be equal");
        assertEquals(user1.hashCode(), user2.hashCode(), "Equal users should have the same hash code");

        // Change one ID and confirm inequality.
        user2.setId(2L);
        assertFalse(user1.equals(user2), "Users with different IDs should not be equal");
    }
}
