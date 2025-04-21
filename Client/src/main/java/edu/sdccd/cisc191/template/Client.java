package edu.sdccd.cisc191.template;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 * The Client class establishes a connection to a server, sends requests, and builds the JavaFX UI for the application.
 * It handles game, user, and size requests, as well as modifications to user data.
 */
public class Client extends Application {
    /**
     * A static user representing the client user. Initialized with name "Chase" and money 1000000.
     */
    public static User user = new User("Chase", 1000000);

    /**
     * The socket used to connect to the server.
     */
    private Socket clientSocket;

    private ObjectOutputStream out; // The output stream for sending requests to the server.
    private ObjectInputStream in; // The input stream for receiving responses from the server.

    // --- Socket and Request Methods ---
    /**
     * Establishes a connection to the server using the provided IP address and port.
     *
     * @param ip   the IP address of the server.
     * @param port the port number on the server.
     * @throws IOException if an I/O error occurs when opening the connection.
     */
    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);

        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
    }

    /**
     * Sends a request to the server and returns a response of the expected type.
     *
     * Never call this method directly, call one of its wrappers for safe usage.
     *
     * @param <T>         the type parameter corresponding to the expected response type.
     * @return the response from the server cast to the specified type.
     * @throws Exception if an error occurs during the request.
     */
    private <T> T sendRequest(Request request, Class<T> responseType) throws Exception {
        // Write request
        out.writeObject(request);
        out.flush();

        // read back whatever the server sent
        Object raw = in.readObject();
        System.out.println("Raw: " + raw);
        System.out.println("Raw type: " + raw.getClass());
        System.out.println("Response Type: " + responseType);

        // cast into the expected type

        try {
            return responseType.cast(raw);
        }
        catch (ClassCastException e) {
            System.out.println("ClassCastException, could not cast " + raw.getClass() + " to " + responseType);
        }

        return null;

    }

    // Update stopConnection to check for null before closing resources:
    public void stopConnection() throws IOException {
        if (in != null) {
            in.close();
        }
        if (out != null) {
            out.close();
        }
        if (clientSocket != null) {
            clientSocket.close();
        }
    }

    /**
     * Modifies a user on the server with the provided attributes and returns the updated user.
     *
     * @param id                 the identifier of the user to modify.
     * @param modifiedAttributes a map containing the fields and their new values.
     *                            Valid Fields: ("Name", "Money", "addBet", "removeBet")
     * @return the updated User object if modification is successful; null otherwise.
     * @throws IOException if an I/O error occurs during the request.
     */
    public User userModifyRequest(int id, Map<String, Object> modifiedAttributes) throws IOException {
        Client client = new Client();
        try {
            client.startConnection("localhost", 4444);
            System.out.println("Sending userModifyRequest with ID: " + id);
            return client.sendRequest(new Request("ModifyUser", id, modifiedAttributes), User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        stopConnection();
        return null;
    }

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
    private HBox createUserInfoBox() {
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
    private HBox createBetListBox(Stage stage) {
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

    /**
     * Retrieves an array of Game objects from the server.
     *
     * @return an array of Game objects.
     * @throws IOException if an I/O error occurs during retrieval.
     */
    private Game[] getGames() throws Exception {
        int size = sendRequest(new Request("GetSize", 1), Integer.class);
        Game[] games = new Game[size];
        for (int i = 0; i < size; i++) {
            games[i] = sendRequest(new Request("Game", 1), Game.class);
        }
        return games;
    }

    private ArrayList<Game> getBasketballGames() throws Exception {
        ArrayList<Game> basketballGames;
        basketballGames = sendRequest(new Request("Basketball", 1), ArrayList.class);
        System.out.println("Basketball games list: " + basketballGames);
        System.out.println(basketballGames.getClass());
        return basketballGames;
    }

    private ArrayList<Game> getFootballGames() throws Exception {
        ArrayList<Game> footballGames;
        footballGames = sendRequest(new Request("Football", 1), ArrayList.class);
        System.out.println("Football games list: " + footballGames);
        System.out.println(footballGames.getClass());
        return footballGames;
    }




    /**
     * The main entry point for the client application.
     * Launches the JavaFX application.
     *
     * @param args command-line arguments.
     */
    public static void main(String[] args) throws IOException {
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
//        System.out.println(getSizeRequest(1));
        startConnection("localhost", 4444);
        System.out.println(getFootballGames());
        // Test modification of user
//        Map<String, Object> attributes = new HashMap<>();
//        attributes.put("Name", "John");
//        attributes.put("Money", 9999);
//
//        System.out.println(userModifyRequest(2, attributes));
//
//        Game[] response = getGames();
//        System.out.println(response);
//
//        // Build the main layout
//        BorderPane borderPane = new BorderPane();
//        borderPane.setPadding(new Insets(20));
//
//
//        // Create UI components
//        TableView<Game> gameTable = createGameTableView(getBasketballGames(), stage);
//        HBox userInfoBox = createUserInfoBox();
//        HBox betListBox = createBetListBox(stage);
//
//        // Assemble components into the BorderPane
//        borderPane.setCenter(gameTable);
//        borderPane.setTop(userInfoBox);
//        borderPane.setBottom(betListBox);
//
//        // Create and set the scene
//        Scene scene = new Scene(borderPane, 1000, 800);
//        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
//        stage.setScene(scene);
//        stage.setTitle("Marauder Bets");
//        stage.show();
    }
} //end class Client

