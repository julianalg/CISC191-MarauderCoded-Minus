package edu.sdccd.cisc191.Client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.sdccd.cisc191.Common.Models.Bet;
import edu.sdccd.cisc191.Common.Models.Game;
import edu.sdccd.cisc191.Common.Models.User;
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

    User user;

    double homeOddStat;
    double awayOddStat;

    /**
     * Initializes the bet view with the specified stage, game, and team.
     * It sets up the necessary data and starts the JavaFX application.
     *
     * @param stage the primary stage for this application.
     * @param game  the game object associated with the bet.
     * @param team  the team on which the bet is being placed.
     * @throws Exception if an error occurs during initialization.
     */
    public void betView(Stage stage, Game game, String team, User user) throws Exception {
        this.game = game;
        System.out.println(team + " is the team");
        this.team = team;
        this.user = user;
        start(stage);
        grabOdds();
    }

    public void grabOdds() throws IOException, ParseException, InterruptedException {
        Client client = new Client();
        System.out.println("Grabbing odds for " + game.getId());
        //Check for int casting later
        try {
            String betInfo = client.getOdds((int) game.getId(), game.getSport());
            System.out.println(betInfo);

            String json = betInfo;

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);

            for (JsonNode bookmaker : root) {
                String bookName = bookmaker.get("name").asText();
                JsonNode bets = bookmaker.get("bets");
                JsonNode firstBet = bets.get(0);

                    if ("Home/Away".equals(firstBet.get("name").asText())) {
                        JsonNode values = firstBet.get("values");
                        // assume values[0] is Home, values[1] is Away
                        homeOddStat = Double.parseDouble(values.get(0).get("odd").asText());
                        awayOddStat = Double.parseDouble(values.get(1).get("odd").asText());
                        System.out.printf(
                                "%s → Home: %s, Away: %s%n",
                                bookName, homeOddStat, awayOddStat
                        );
                        break; // stop scanning other markets for this bookmaker
                    }
            }
        } catch (Exception e) {
            System.out.println("Error grabbing odds"  + e.getMessage());
            homeOddStat = 2.25;
            awayOddStat = 2.25;
        }
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
        Button b2 = new Button("Cancel");

        betView.getChildren().addAll(bet, b, b1, b2);

        b1.setOnAction(evt -> {
            Integer amount = Integer.parseInt(b.getText());
            int winAmt;
            if (team.equals(game.getTeam1())) {
                winAmt = (int) (amount * homeOddStat);
            } else {
                winAmt = (int) (amount * awayOddStat);
            }
            if (user.getMoneyBet() >= amount) {
                //Creating a dialog
                Dialog<String> dialog = new Dialog<>();
                //Setting the title
                dialog.setTitle("Marauder Bets");
                ButtonType type = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
                //Setting the content of the dialog
                dialog.setContentText("You bet " + amount + " and you will get " + winAmt);
                //Adding buttons to the dialog pane
                dialog.getDialogPane().getButtonTypes().add(type);
                dialog.showAndWait();
                System.out.println("Betting on " + game.getDbId() + " for " + amount + " and winning " + winAmt);
                try {
                    Client.patchAddBetToMainUser(1L, game.getDbId(), team, amount, winAmt);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
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
                dialog.setContentText("This is more money than you have available to bet! $" + user.getMoneyBet());
                //Adding buttons to the dialog pane
                dialog.getDialogPane().getButtonTypes().add(type);
                dialog.showAndWait();
            }
        });
        b2.setOnAction(evt -> {
            UI ui = new UI();
            try {
                ui.start(stage);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        stage.setScene(new Scene(betView, 200, 300));
        stage.show();
    }
}
