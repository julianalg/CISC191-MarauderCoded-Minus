package edu.sdccd.cisc191.Client;

import edu.sdccd.cisc191.Common.Models.Game;
import edu.sdccd.cisc191.Common.Models.User;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.testfx.api.FxAssert.verifyThat;

public class BetViewTest extends ApplicationTest {
    private BetView betView;
    private User testUser;
    private Game testGame;

    @Override
    public void start(Stage stage) throws Exception {
        setupTestData();
        // Use a fresh BetView for each test
        betView = new BetView();
        betView.betView(stage, testGame, testGame.getTeam1(), testUser);
    }

    private void setupTestData() {
        // Create a test user with $100 available to bet
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("TestUser");
        testUser.setMoney(1000);
        testUser.setMoneyLine(100);
        testUser.setMoneyBet(100);

        // Create a test game
        testGame = new Game(
                "Team1",
                "Team2",
                42L,
                new Date(),
                "Basketball",
                1.5,
                2.5
        );
        testGame.setGameDate(new DateTime());
    }

    @Test
    public void testUIComponentsExist() {
        // The prompt label
        verifyThat(".label", LabeledMatchers.hasText("How much do you want to bet?"));

        // The text field for entering a bet
        TextField betField = lookup(".text-field").query();
        assertNotNull(betField);

        // The two buttons
        Button placeButton = lookup("Place Bet").queryButton();
        assertNotNull(placeButton);

        Button cancelButton = lookup("Cancel").queryButton();
        assertNotNull(cancelButton);
    }

    @Test
    public void testCancelButtonReturnsToMainUI() {
        // Clicking "Cancel" should take us back to the main UI, which contains a TableView
        clickOn("Cancel");
        TableView<?> mainTable = lookup(".table-view").queryTableView();
        assertNotNull(mainTable);
    }

    @Test
    public void testInsufficientFundsDialog() {
        // Enter an amount greater than the user's available money
        clickOn(".text-field").write(String.valueOf(testUser.getMoneyBet() + 50));
        clickOn("Place Bet");

        // Verify the error dialog content
        verifyThat(".dialog-pane .content",
                LabeledMatchers.hasText("This is more money than you have available to bet! $" + testUser.getMoneyBet()));
    }

    @Test
    public void testPlaceBetSuccessDialog() {
        // Stub out the static call to patchAddBetToMainUser so it doesn't actually hit the server
        try (MockedStatic<Client> clientStatic = mockStatic(Client.class)) {
            clientStatic
                    .when(() -> Client.patchAddBetToMainUser(
                            anyLong(), anyLong(), anyString(), anyInt(), anyInt()))
                    .thenAnswer(invocation -> null);

            // Override UI.start to be a no-op to prevent reloading the UI
            try (var uiMock = mockConstruction(UI.class, (mock, ctx) -> {
                doNothing().when(mock).start(any(Stage.class));
            })) {
                // Enter a valid bet (1), which will use grabOdds and fall back to default 2.25
                clickOn(".text-field").write("1");
                clickOn("Place Bet");

                // The win amount should be (int)(1 * 2.25) = 2
                verifyThat(".dialog-pane .content",
                        LabeledMatchers.hasText("You bet 1 and you will get 2"));
            }
        }
    }
}
