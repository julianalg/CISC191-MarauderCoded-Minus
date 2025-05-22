package edu.sdccd.cisc191.Client;

import edu.sdccd.cisc191.Common.Bet;
import edu.sdccd.cisc191.Common.Models.Game;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

/**
 * The BetView class is a view class that creates
 * the JavaFX window seen by the User after pressing the Bet button.
 * It processes the bet amount entered by the user
 */
public class BetView extends Application {
    /**
     * The current game for which the bet is being placed.
     */
    Game game;

    /**
     * The team on which the bet is being placed.
     */
    String team;

    /**
     * Initializes the bet view with the specified stage, game, and team.
     * It sets up the necessary data and starts the JavaFX application.
     *
     * @param stage the primary stage for this application.
     * @param game  the game object associated with the bet.
     * @param team  the team on which the bet is being placed.
     * @throws Exception if an error occurs during initialization.
     */
    public void betView(Stage stage, Game game, String team) throws Exception {
        this.game = game;
        this.team = team;
        start(stage);
        grabOdds();
    }

    public void grabOdds() throws IOException, ParseException {
        Client client = new Client();
        System.out.println("Grabbing odds for " + game.getId());
        //Check for int casting later
        String betInfo = client.oddsModifyRequest((int) game.getId());
        JSONParser jsonParser = new JSONParser();
        JSONArray betObj = (JSONArray) jsonParser.parse(betInfo);
        JSONObject bookmaker = (JSONObject) betObj.get(0);
        System.out.println(bookmaker.get("bets"));

    }

    /**
     * Starts the JavaFX application by setting up the user interface for placing a bet.
     * The interface includes a label, a text field for entering the bet amount, and a button to place the bet.
     * When the bet is placed, the amount is validated against the user's available funds.
     *
     * @param stage the primary stage for this application.
     * @throws Exception if an error occurs while setting up the scene.
     */
    @Override
    public void start(Stage stage) throws Exception {

        VBox betView = new VBox(10);
        Label bet = new Label("How much do you want to bet?");
        TextField b = new TextField();
        Button b1 = new Button("Place Bet");

        betView.getChildren().addAll(bet, b, b1);

        b1.setOnAction(evt -> {
            Integer amount = Integer.parseInt(b.getText());
            if (0 >= amount) {
                Bet placedBet = new Bet(game, amount, team);
//                Client.user.addBet(placedBet);
                try {
                    new UI().start(stage);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                //Creating a dialog
                Dialog<String> dialog = new Dialog<>();
                //Setting the title
                dialog.setTitle("Marauder Bets");
                ButtonType type = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
                //Setting the content of the dialog
//                dialog.setContentText("This is more money than you have available to bet! $" + Client.user.getMoneyBet());
                //Adding buttons to the dialog pane
                dialog.getDialogPane().getButtonTypes().add(type);
                dialog.showAndWait();
            }
        });
        stage.setScene(new Scene(betView, 200, 300));
        stage.show();
    }
}
