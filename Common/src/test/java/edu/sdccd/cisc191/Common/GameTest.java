package edu.sdccd.cisc191.Common;

import static org.junit.jupiter.api.Assertions.*;

import edu.sdccd.cisc191.Common.Models.Game;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

import java.util.Date;

/**
 * Unit tests for the Game class.
 */
public class GameTest {

    @Test
    public void testGetDateClean() {
        // Create a Game with a specific date and time.
        // Example: May 22, 2025, at 18:32.
        DateTime dt = new DateTime(2025, 5, 22, 18, 32);
        Game game = new Game("Team A", "Team B", 1, dt.toDate(), "Basketball", 2.0, 3.0);
        String dateClean = game.getDateClean();
        // Expecting the clean date to be in the format "month/day/year hour:minute"
        assertEquals("5/22/2025 18:32", dateClean, "Clean date string should match expected format");
    }

    @Test
    public void testToString() {
        DateTime dt = new DateTime(2025, 5, 22, 18, 32);
        Game game = new Game("Team A", "Team B", 1, dt.toDate(), "Basketball", 2.0, 3.0);
        String result = game.toString();
        // Check that the toString() output contains key team names and components of the date.
        assertTrue(result.contains("Team A"), "toString should contain team1 name");
        assertTrue(result.contains("Team B"), "toString should contain team2 name");
        assertTrue(result.matches(".*\\d{1,2}/\\d{1,2}/\\d{4}.*"), "toString should contain the game date");
    }
}
