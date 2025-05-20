package edu.sdccd.cisc191.Client;

import edu.sdccd.cisc191.Common.Models.Game;
import edu.sdccd.cisc191.Common.Models.*;
import edu.sdccd.cisc191.Common.Bet;
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
    private <T extends Game> TableView<T> createGameTableView(T[] games, Stage stage) {
        TableView<T> tableView = new TableView<>();

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

        // Column for Team 1 Odds
        TableColumn<T, String> team1OddCol = new TableColumn<>("Team 1 Odds");
        team1OddCol.setCellValueFactory(new PropertyValueFactory<>("team1Odd"));
        tableView.getColumns().add(team1OddCol);
        team1OddCol.setResizable(false);
        team1OddCol.setSortable(false);
        team1OddCol.setReorderable(false);

        // Button column for betting on Team 1
        TableColumn<T, Void> bet1Column = new TableColumn<>("Bet");
        bet1Column.setCellFactory(column -> new TableCell<T, Void>() {
            private final Button betButton = new Button("Bet");
            {
                betButton.setOnAction(event -> {
                    int index = getIndex();
                    if (index >= 0 && index < getTableView().getItems().size()) {
                        T game = getTableView().getItems().get(index);
                        try {
                            new BetView().betView(stage, game, game.getTeam1());
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
                        betButton.setDisable(user.checkBet(game));
                    }
                    setGraphic(betButton);
                }
            }
        });
        tableView.getColumns().add(bet1Column);
        bet1Column.setResizable(false);
        bet1Column.setSortable(false);
        bet1Column.setReorderable(false);

        // Column for Team 2 Odds
        TableColumn<T, String> team2OddCol = new TableColumn<>("Team 2 Odds");
        team2OddCol.setCellValueFactory(new PropertyValueFactory<>("team2Odd"));
        tableView.getColumns().add(team2OddCol);
        team2OddCol.setResizable(false);
        team2OddCol.setSortable(false);
        team2OddCol.setReorderable(false);

        // Button column for betting on Team 2
        TableColumn<T, Void> bet2Column = new TableColumn<>("Bet");
        bet2Column.setCellFactory(column -> new TableCell<T, Void>() {
            private final Button betButton = new Button("Bet");
            {
                betButton.setOnAction(event -> {
                    int index = getIndex();
                    if (index >= 0 && index < getTableView().getItems().size()) {
                        T game = getTableView().getItems().get(index);
                        try {
                            new BetView().betView(stage, game, game.getTeam2());
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
                        betButton.setDisable(user.checkBet(game));
                    }
                    setGraphic(betButton);
                }
            }
        });
        tableView.getColumns().add(bet2Column);
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

    static ArrayList<Game> allGames;
    static User user;

    /**
     * The main entry point for the client application.
     * Launches the JavaFX application.
     */
    public static void init(ArrayList<Game> games, User u) throws IOException {
        System.out.println("Helloooo");
        allGames = games;
        user = u;
        launch();// Run this Application.
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
        // Build the main layout
        System.out.println(allGames);
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(20));

        // Create UI components
        TableView<Game> gameTable = createGameTableView(allGames.toArray(new Game[0]), stage);
        HBox userInfoBox = createUserInfoBox(user);
        HBox betListBox = createBetListBox(stage, user);

        // Assemble components into the BorderPane
        borderPane.setCenter(gameTable);
        borderPane.setTop(userInfoBox);
        borderPane.setBottom(betListBox);

        // Create and set the scene
        Scene scene = new Scene(borderPane, 1000, 800);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Marauder Bets");
        stage.show();
    }

}
