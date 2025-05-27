package edu.sdccd.cisc191.Client;

import edu.sdccd.cisc191.Common.Models.Bet;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;

public class BetInfoViewTest extends ApplicationTest {
    private BetInfoView view;
    private Bet testBet;
    private double[][] oddsData;

    @Override
    public void start(Stage stage) throws Exception {
        buildTestBet();
        view = new BetInfoView();
        view.betInfoView(stage, testBet);
    }

    private void buildTestBet() {
        // dummy odds-over-time: [odd, timestampSeconds]
        oddsData = new double[][] {
                {1.2, 1620000000L},
                {1.5, 1620003600L},
                {2.0, 1620007200L}
        };

        // real Bet subclass with stable toString()
        testBet = new Bet() {
            @Override
            public String toString() {
                return "Bet[id=1, amt=50, win=75]";
            }
        };
        testBet.setBetAmt(50);
        testBet.setWinAmt(75);
    }

    @Test
    public void testLabelsShowBetInfoAndAmounts() {
        // The header label should reflect our override of toString()
        verifyThat(".label", LabeledMatchers.hasText("Bet[id=1, amt=50, win=75]"));

        // There should be two labels in the HBox for money:
        // one for betAmt and one for winAmt
        verifyThat("$50", LabeledMatchers.hasText("$50"));
        verifyThat("$75", LabeledMatchers.hasText("$75"));
    }


    @Test
    public void testAxisLabelsAreCorrect() {
        @SuppressWarnings("unchecked")
        LineChart<String, Number> chart = lookup(".chart").query();
        assertEquals("Time", chart.getXAxis().getLabel());
        assertEquals("Odds", chart.getYAxis().getLabel());
    }

    @Test
    public void testBackButtonStyling() {
        Button back = lookup("Back").queryButton();
        assertNotNull(back);
        assertTrue(back.getStyleClass().contains("primary-button"));
    }

    @Test
    public void testBackButtonNavigatesToMainUI() {
        // Clicking "Back" should load the main UI table again
        clickOn("Back");
        TableView<?> mainTable = lookup(".table-view").queryTableView();
        assertNotNull(mainTable);
    }
}
