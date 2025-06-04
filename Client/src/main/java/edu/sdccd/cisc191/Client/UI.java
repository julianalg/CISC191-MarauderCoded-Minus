package edu.sdccd.cisc191.Client;

import edu.sdccd.cisc191.Common.Models.Game;
import edu.sdccd.cisc191.Common.Models.*;
import edu.sdccd.cisc191.Common.Models.Bet;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class UI extends Application {
    // --- UI Component Builders ---
    // Build the table view for the games
    /**
     * Creates a TableView for displaying game information.
     * The table includes columns for teams, game date, odds, and betting buttons.
     *
     * @param games the array of Game objects to display.
     * @param stage the Stage on which the TableView is displayed.
     * @return a TableView populated with game data.
     */
    private <T extends Game> TableView<T> createGameTableView(T[] games, User user, Stage stage) {
        TableView<T> tableView = new TableView<>();
         // TODO: Add a null check for the games array to prevent crashing if it's null

        // Column for Team 1
        TableColumn<T, String> team1Col = new TableColumn<>("Team 1");
        team1Col.setCellValueFactory(new PropertyValueFactory<>("team1"));
        tableView.getColumns().add(team1Col);
        team1Col.setPrefWidth(150);
        team1Col.setResizable(false);
        team1Col.setSortable(false);
        team1Col.setReorderable(false);

        // Column for Team 2
        TableColumn<T, String> team2Col = new TableColumn<>("Team 2");
        team2Col.setCellValueFactory(new PropertyValueFactory<>("team2"));
        tableView.getColumns().add(team2Col);
        team2Col.setResizable(false);
        team2Col.setPrefWidth(150);
        team2Col.setSortable(false);
        team2Col.setReorderable(false);

        // Column for Sport
        TableColumn<T, String> sportCol = new TableColumn<>("Sport");
        sportCol.setCellValueFactory(new PropertyValueFactory<>("sport"));
        tableView.getColumns().add(sportCol);
        sportCol.setResizable(false);
        sportCol.setSortable(false);
        sportCol.setReorderable(false);


        // Column for Date
        TableColumn<T, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateClean"));
        dateCol.setPrefWidth(175);
        tableView.getColumns().add(dateCol);
        dateCol.setResizable(false);
        dateCol.setSortable(false);
        dateCol.setReorderable(false);


        // Button column for betting on Team 1
        TableColumn<T, Void> bet1Column = new TableColumn<>("Bet Team 1");
        bet1Column.setCellFactory(column -> new TableCell<T, Void>() {
            private final Button betButton = new Button("Bet 1");
            {
                betButton.setOnAction(event -> {
                    int index = getIndex();
                    if (index >= 0 && index < getTableView().getItems().size()) {
                        T game = getTableView().getItems().get(index);
                        try {
                            new BetView().betView(stage, game, game.getTeam1(), user);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    betButton.getStyleClass().add("primary-button");
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    int index = getIndex();
                    if (index >= 0 && index < getTableView().getItems().size()) {
                        T game = getTableView().getItems().get(index);
//                        betButton.setDisable(user.checkBet(game));
                    }
                    setGraphic(betButton);
                }
            }
        });
        tableView.getColumns().add(bet1Column);
        bet1Column.setPrefWidth(200);
        bet1Column.setResizable(false);
        bet1Column.setSortable(false);
        bet1Column.setReorderable(false);

        // Button column for betting on Team 2
        TableColumn<T, Void> bet2Column = new TableColumn<>("Bet Team 2");
        bet2Column.setCellFactory(column -> new TableCell<T, Void>() {
            private final Button betButton = new Button("Bet 2");
            {
                betButton.setOnAction(event -> {
                    int index = getIndex();
                    if (index >= 0 && index < getTableView().getItems().size()) {
                        T game = getTableView().getItems().get(index);
                        try {
                            new BetView().betView(stage, game, game.getTeam2(), user);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                betButton.getStyleClass().add("primary-button");
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    int index = getIndex();
                    if (index >= 0 && index < getTableView().getItems().size()) {
                        T game = getTableView().getItems().get(index);
//                        betButton.setDisable(user.checkBet(game));
                    }
                    setGraphic(betButton);
                }
            }
        });
        tableView.getColumns().add(bet2Column);
        bet2Column.setPrefWidth(200);
        bet2Column.setResizable(false);
        bet2Column.setSortable(false);
        bet2Column.setReorderable(false);

        tableView.getStyleClass().add("custom-table");

        // Add game items to the table view
        tableView.getItems().addAll(games);

        return tableView;
    }

    /**
     * Creates an HBox containing user information labels.
     *
     * @return an HBox with the user's name, money, money line, and money bet.
     */
    private HBox createUserInfoBox(User user) {
        HBox userInfo = new HBox(10);
        userInfo.setBackground(Background.fill(Color.rgb(126, 24, 145)));

        Label userName = new Label(user.getName());
        userName.setId("userNameLabel");
        userName.setFont(new Font(20));
        userName.setTextFill(Color.WHITE);
 // TODO: Format money to two decimal places so it looks cleaner (like $10.00)
        Label money = new Label("$" + user.getMoney());
        money.setFont(new Font(20));
        money.setTextFill(Color.WHITE);

        Label moneyLine = new Label("$" + user.getMoneyLine());
        moneyLine.setFont(new Font(20));
        moneyLine.setTextFill(Color.LIGHTGRAY);

        Label moneyBet = new Label("$" + user.getMoneyBet());
        moneyBet.setFont(new Font(20));
        moneyBet.setTextFill(Color.LIGHTGRAY);

        userInfo.getChildren().addAll(userName, money, moneyLine, moneyBet);
        return userInfo;
    }

    /**
     * Creates an HBox that displays the list of bets placed by the user.
     * If no bets are present, a placeholder label is shown.
     *
     * @param stage the Stage on which the bet list is displayed.
     * @return an HBox containing bet information.
     */
    private HBox createBetListBox(Stage stage, User user) {
        HBox betList = new HBox(10);
        betList.setPrefHeight(200);
// TODO: Use a ScrollPane here if the user has many bets to avoid layout issues
        if (user.getBets().isEmpty()) {
            Label emptyLabel = new Label("Your bets will appear here");
            emptyLabel.setId("betListPlaceholderLabel");
            emptyLabel.setFont(Font.font("System", FontPosture.ITALIC, 12));
            betList.getChildren().add(emptyLabel);
        } else {
            for (Bet bet : user.getBets()) {
                VBox betBox = new VBox(10);
                betBox.setPrefHeight(200);
                betBox.setPrefWidth(200);
                betBox.getStyleClass().add("bet-box");
// TODO: If team names are long, consider shortening them or adding ellipses; quality of life
                Label gameLabel = new Label(bet.getGame().getTeam1() + " vs " + bet.getGame().getTeam2());
                Label dateLabel = new Label(bet.getGame().getDateClean());
                Label teamLabel = new Label(bet.getBetTeam());

                // Apply a common style class to all bet-related labels
                gameLabel.getStyleClass().add("bet-box-label");
                dateLabel.getStyleClass().add("bet-box-label");
                teamLabel.getStyleClass().add("bet-box-label");

                Label betAmt = new Label("Bet $" + bet.getBetAmt());
                betAmt.setFont(new Font(10));
                betAmt.setTextFill(Color.WHITE);
                Label winAmt = new Label("Win $" + bet.getWinAmt());
                winAmt.setFont(new Font(10));
                winAmt.setTextFill(Color.WHITE);

                Button betInfo = new Button("See More");
                betInfo.getStyleClass().add("bet-info-button");
                betInfo.setOnAction(event -> {
                    try {
                        new BetInfoView().betInfoView(stage, bet);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

                betBox.getChildren().addAll(gameLabel, dateLabel, teamLabel, betAmt, winAmt, betInfo);
                betList.getChildren().add(betBox);
            }
        }
        return betList;
    }

    /**
     * Creates a VBox listing other users in the game, showing each user's
     * name and current money.
     *
     * @param players the list of other User objects to display
     * @return a VBox containing one HBox per player
     */
    private VBox createOtherPlayersBox(ArrayList<BotBase> players) {
        VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        container.getStyleClass().add("other-players-box"); // for custom styling
         // TODO: If the player list is long, wrap container in a ScrollPane to make it scrollable; quality of life

        // Title
        Label title = new Label("Other Players");
        title.setFont(new Font(18));
        title.setTextFill(Color.BLACK);
        container.getChildren().add(title);

        // One row per player
        for (BotBase pl : players) {
            User p = pl.getUser();
            HBox row = new HBox(15);
            row.setPadding(new Insets(5));
            row.getStyleClass().add("other-player-row");

            Label nameLabel = new Label(p.getName());
            nameLabel.setFont(new Font(14));
            nameLabel.setTextFill(Color.BLACK);

            Label moneyLabel = new Label("$" + p.getMoney());
            moneyLabel.setFont(new Font(14));
            moneyLabel.setTextFill(Color.LIGHTGREEN);

            row.getChildren().addAll(nameLabel, moneyLabel);
            container.getChildren().add(row);
        }

        return container;
    }



    @Override
    /**
     * Starts the JavaFX application by building the main layout.
     * It sets up UI components such as the game table, user info box, and bet list,
     * and then displays the primary stage.
     *
     * @param stage the primary Stage for this application.
     * @throws Exception if an error occurs during initialization.
     */
    public void start(Stage stage) throws Exception {
        User mainUser = Client.getMainUser();
        ArrayList<Game> allGames  = Client.getGames();
        Client.updateBets();

        // Build the main layout
        System.out.println(allGames);
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(20));

        // Create UI components
        TableView<Game> gameTable = createGameTableView(allGames.toArray(new Game[0]), mainUser, stage);
        HBox userInfoBox = createUserInfoBox(mainUser);
        HBox betListBox = createBetListBox(stage, mainUser);
        VBox otherPlayersBox = createOtherPlayersBox(Client.getBots());

        // Assemble components into the BorderPane
        borderPane.setCenter(gameTable);
        borderPane.setTop(userInfoBox);
        borderPane.setBottom(betListBox);
        borderPane.setRight(otherPlayersBox);

        // Create and set the scene
        Scene scene = new Scene(borderPane, 1500, 1500);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        // TODO: Add a check to make sure the CSS file is found as to avoid a null pointer exception
        stage.setScene(scene);
        stage.setTitle("Marauder Bets");
        stage.show();
    }

}
