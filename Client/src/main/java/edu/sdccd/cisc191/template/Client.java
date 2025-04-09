package edu.sdccd.cisc191.template;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.*;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
    /**
     * The output stream for sending requests to the server.
     */
    private PrintWriter out;
    /**
     * The input stream for receiving responses from the server.
     */
    private BufferedReader in;

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
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    /**
     * Sends a request to the server and returns a response of the expected type.
     *
     * Never call this method directly, call one of its wrappers for safe usage.
     *
     * @param requestType the type of request to send. For example: "Game", "User", "GetSize".
     * @param id          the identifier for the requested data.
     * @param returnType  the expected class type of the response.
     * @param <T>         the type parameter corresponding to the expected response type.
     * @return the response from the server cast to the specified type.
     * @throws Exception if an error occurs during the request.
     */
    // In the sendRequest method, add a null check for the response:
    private <T> T sendRequest(String requestType, int id, Map<String, Object> modifiedRequest, Class<T> returnType) throws Exception {
        out.println(Request.toJSON(new Request(requestType, id, modifiedRequest)));
        String response = in.readLine();
        if (response == null) {
            throw new IllegalArgumentException("No response received from server");
        }

        if (returnType == Game.class) {
            return returnType.cast(Game.fromJSON(response));
        } else if (returnType == User.class) {
            return returnType.cast(User.fromJSON(response));
        } else if (returnType == Integer.class) {
            return returnType.cast(Integer.parseInt(response));
        } else {
            throw new IllegalArgumentException("Unsupported return type: " + returnType.getName());
        }
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
     * Retrieves a game object from the server by the specified ID.
     *
     * @param id the identifier of the game to retrieve.
     * @return the Game object if found; null otherwise.
     * @throws IOException if an I/O error occurs during the request.
     */
    public Game gameGetRequest(int id) throws IOException {
        Client client = new Client();
        try {
            client.startConnection("localhost", 4444);
            System.out.println("Sending gameGetRequest with ID: " + id);
            return client.sendRequest("Game", id, null, Game.class);
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                client.stopConnection();
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    /**
     * Retrieves a user object from the server by the specified ID.
     *
     * @param id the identifier of the user to retrieve.
     * @return the User object if found; null otherwise.
     * @throws IOException if an I/O error occurs during the request.
     */
    public User userGetRequest(int id) throws IOException {
        Client client = new Client();
        try {
            client.startConnection("localhost", 4444);
            System.out.println("Sending userRequest with ID: " + id);
            return client.sendRequest("User", id, null, User.class);
        } catch(Exception e) {
            e.printStackTrace();
        }

        stopConnection();
        return null;
    }

    /**
     * Retrieves the size of a resource (e.g., number of games or users) from the server.
     *
     * @param id the identifier for the resource size request.
     *           (1 for gameDatabase and 2 for userDatabase)
     * @return the size of the requested resource; -1 if an error occurs.
     * @throws IOException if an I/O error occurs during the request.
     */
    public int getSizeRequest(int id) throws IOException {

        Client client = new Client();
        try {
            client.startConnection("localhost", 4444);
            System.out.println("Sending getSizeRequest with ID: " + id);
            return client.sendRequest("GetSize", id, null, Integer.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        stopConnection();
        return -1;
    };

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
            return client.sendRequest("ModifyUser", id, modifiedAttributes, User.class);
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
        team1Col.setResizable(false);
        team1Col.setSortable(false);
        team1Col.setReorderable(false);

        // Column for Team 2
        TableColumn<T, String> team2Col = new TableColumn<>("Team 2");
        team2Col.setCellValueFactory(new PropertyValueFactory<>("team2"));
        tableView.getColumns().add(team2Col);
        team2Col.setResizable(false);
        team2Col.setSortable(false);
        team2Col.setReorderable(false);

        // Column for Date
        TableColumn<T, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateClean"));
        dateCol.setPrefWidth(150);
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
    private Game[] getGames() throws IOException {
        int size = getSizeRequest(1);
        Game[] games = new Game[size];
        for (int i = 0; i < size; i++) {
            games[i] = this.gameGetRequest(i);
        }
        return games;
    }



    /**
     * The main entry point for the client application.
     * Launches the JavaFX application.
     *
     * @param args command-line arguments.
     */
    public static void main(String[] args) {
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
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(20));

        // Retrieve game data (and user data if needed)
        Game[] games = getGames();
        // Note: The sample user array in the original code is replaced by the static "user" field.
        // The below array is never used, but is shown as a demonstration of potential code
        // (We never used it because the leaderboard designs we thought up looked ugly).
        User[] users = new User[]{
                userGetRequest(0),
                userGetRequest(1),
                userGetRequest(2),
                userGetRequest(3),
                userGetRequest(4),
        };

        // Example modification of user
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("Name", "John");
        attributes.put("Money", 9999);
        // Serialize Bet object into JSON string before sending to server
        attributes.put("addBet", Bet.toJSON(new Bet(games[0], 100, games[0].getTeam1())));
        userModifyRequest(2, attributes);
        // --- END EXAMPLE CODE ---

        // Create UI components
        TableView<Game> gameTable = createGameTableView(games, stage);
        HBox userInfoBox = createUserInfoBox();
        HBox betListBox = createBetListBox(stage);

        // Assemble components into the BorderPane
        borderPane.setCenter(gameTable);
        borderPane.setTop(userInfoBox);
        borderPane.setBottom(betListBox);

        // Create and set the scene
        Scene scene = new Scene(borderPane, 800, 800);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Marauder Bets");
        stage.show();
    }
} //end class Client

