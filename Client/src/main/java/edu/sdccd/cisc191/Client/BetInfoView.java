package edu.sdccd.cisc191.Client;

import edu.sdccd.cisc191.Common.Models.Bet;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * BetInfoView displays detailed information about a bet, including the bet amount, win amount,
 * and the win odds over time. It uses a JavaFX application to render the UI components such as
 * labels, charts, and buttons to allow users to view bet details and navigate back to the main view.
 *
 *  Usage:
 * <pre>
 *   BetInfoView view = new BetInfoView();
 *   view.betInfoView(primaryStage, bet);
 * </pre>
 *
 */
public class BetInfoView extends Application {
    /**
     * The bet object containing details to be displayed in the view.
     */
    Bet bet;

    /**
     * Launches the BetInfoView for a given Bet object.
     *
     * @param primaryStage the primary stage for the JavaFX application.
     * @param bet the Bet object containing bet details.
     * @throws Exception if an error occurs during view initialization.
     */
    public void betInfoView(Stage primaryStage, Bet bet) throws Exception {
        this.bet = bet;
        start(primaryStage);
    }

    /**
     * Starts the JavaFX application by constructing the scene graph to display bet information.
     * It builds a layout with bet details, monetary values, and a line chart showing win odds over time.
     *
     * @param stage the primary stage for the JavaFX application.
     * @throws Exception if an error occurs during UI construction.
     */
    @Override
    public void start(Stage stage) throws Exception {
        VBox root = new VBox(10);
        root.setStyle("-fx-alignment: center");

        Label betInfo = new Label(bet.toString());

        HBox money = new HBox(5);
        Label betAmt = new Label("$" + bet.getBetAmt());
        Label winAmt = new Label("$" + bet.getWinAmt());
        money.getChildren().addAll(betAmt, winAmt);
        money.setStyle("-fx-alignment: center");

        // Set up the axes for the line chart.
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Time");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Odds");

        // Create the LineChart.
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Win Odds Over Time");

        // Create a data series.
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Win Odds");



        root.getChildren().addAll(betInfo, money, lineChart);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        stage.setTitle("Bet Info");
        stage.setScene(scene);
        stage.show();
    }
}
