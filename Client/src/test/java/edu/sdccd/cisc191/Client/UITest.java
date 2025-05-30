package edu.sdccd.cisc191.Client;

import edu.sdccd.cisc191.Common.Models.Game;
import edu.sdccd.cisc191.Common.Models.User;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

public class UITest extends ApplicationTest {
    private UI ui;
    private User testUser;
    private ArrayList<Game> testGames;

    @Override
    public void start(Stage stage) throws Exception {
        // Initialize test data before starting the UI
        setupTestData();
        
        // Set the static fields in UI
        this.testGames = Client.getGames();
        this.testUser = Client.getMainUser() ;
        
        // Create and start the UI
        ui = new UI();
        ui.start(stage);
    }

    private void setupTestData() {
        // Create test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("TestUser");
        testUser.setMoney(1000);
        testUser.setMoneyLine(100);
        testUser.setMoneyBet(200);

        // Create test games
        testGames = new ArrayList<>();
        Game game1 = new Game("1", "2", 99999, new Date(), "Basketball", 1, 2);
        game1.setGameDate(new DateTime());

        Game game2 = new Game("1", "2", 99999, new Date(), "Basketball", 1, 2);
        game2.setGameDate(DateTime.now().plusDays(1));

        testGames.add(game1);
        testGames.add(game2);
    }

   /* @Test
    public void testUserInfoDisplay() {
        // Verify user information is displayed correctly
        verifyThat("#userNameLabel", LabeledMatchers.hasText(testUser.getName()));
        
        // Find money labels and verify their content
        Label moneyLabel = lookup(".label").match(label -> 
            label.equals("$" + testUser.getMoney())).query();
        assertNotNull(moneyLabel);
        assertEquals("$" + testUser.getMoney(), moneyLabel.getText());
    }*/

    @Test
    public void testGameTableContent() {
        // Get the TableView
        @SuppressWarnings("unchecked")
        TableView<Game> gameTable = lookup(".table-view").queryTableView();
        
        // Verify table is not null and has correct number of items
        assertNotNull(gameTable);
        assertEquals(testGames.size(), gameTable.getItems().size());
        
        // Verify content of first game
        Game firstGame = gameTable.getItems().get(0);
        assertEquals(testGames.get(0).getTeam1(), firstGame.getTeam1());
        assertEquals(testGames.get(0).getTeam2(), firstGame.getTeam2());
        assertEquals(testGames.get(0).getSport(), firstGame.getSport());
    }

    @Test
    public void testBetListDisplay() {
        verifyThat(".label", LabeledMatchers.hasText("Team 1"));
    }

    @Test
    public void testTableColumns() {
        @SuppressWarnings("unchecked")
        TableView<Game> gameTable = lookup(".table-view").queryTableView();
        
        // Verify all required columns are present
        assertTrue(gameTable.getColumns().stream()
                .anyMatch(col -> col.getText().equals("Team 1")));
        assertTrue(gameTable.getColumns().stream()
                .anyMatch(col -> col.getText().equals("Team 2")));
        assertTrue(gameTable.getColumns().stream()
                .anyMatch(col -> col.getText().equals("Sport")));
        assertTrue(gameTable.getColumns().stream()
                .anyMatch(col -> col.getText().equals("Date")));
        assertTrue(gameTable.getColumns().stream()
                .anyMatch(col -> col.getText().equals("Bet Team 1")));
        assertTrue(gameTable.getColumns().stream()
                .anyMatch(col -> col.getText().equals("Bet Team 2")));
    }

    @Test
    public void testBetButtonsEnabled() {
        // Verify bet buttons are enabled when user hasn't placed bets
        assertTrue(lookup("Bet 1").queryAll().stream()
                .allMatch(node -> !node.isDisabled()));
        assertTrue(lookup("Bet 2").queryAll().stream()
                .allMatch(node -> !node.isDisabled()));
    }

    @Test
    public void testTableViewStyling() {
        TableView<?> tableView = lookup(".custom-table").queryTableView();
        assertTrue(tableView.getStyleClass().contains("custom-table"));
    }
}