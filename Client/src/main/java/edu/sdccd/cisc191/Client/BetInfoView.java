package edu.sdccd.cisc191.Client;

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

        // Assume bet.getWinOddsOvertime() returns a double[][] where each element is [odd, timestamp]
        double[][] oddsData = bet.getWinOddsOvertime();
        // Iterate over each pair in the data array.
        for (int i = 0; i < oddsData.length; i++) {
            double odd = oddsData[i][0];
            // The timestamp is stored as seconds since epoch.
            long timestamp = (long) oddsData[i][1];
            // Format the timestamp (multiply by 1000 to convert to milliseconds).
            String timeLabel = new SimpleDateFormat("HH:mm").format(new Date(timestamp * 1000));
            series.getData().add(new XYChart.Data<>(timeLabel, odd));
        }

        // Add the series to the chart.
        lineChart.getData().add(series);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            try {
                new UI().start(stage);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        backButton.getStyleClass().add("primary-button");

        root.getChildren().addAll(betInfo, money, lineChart, backButton);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        stage.setTitle("Bet Info");
        stage.setScene(scene);
        stage.show();
    }
}
