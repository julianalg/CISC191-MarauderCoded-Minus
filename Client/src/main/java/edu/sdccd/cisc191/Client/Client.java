package edu.sdccd.cisc191.Client;

import edu.sdccd.cisc191.Common.Models.Game;
import edu.sdccd.cisc191.Common.Models.User;
import edu.sdccd.cisc191.Common.Request;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;

/**
 * The Client class establishes a connection to a server, sends requests, and builds the JavaFX UI for the application.
 * It handles game, user, and size requests, as well as modifications to user data.
 */
public class Client {
    /**
     * A static user representing the client user. Initialized with name "Chase" and money 1000000.
     */

    /**
     * The socket used to connect to the server.
     */
    private static Socket clientSocket;

    private static ObjectOutputStream out; // The output stream for sending requests to the server.
    private static ObjectInputStream in; // The input stream for receiving responses from the server.

    // --- Socket and Request Methods ---
    /**
     * Establishes a connection to the server using the provided IP address and port.
     *
     * @param ip   the IP address of the server.
     * @param port the port number on the server.
     * @throws IOException if an I/O error occurs when opening the connection.
     */
    public static void startConnection(String ip, int port) throws IOException {
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
    static <T> T sendRequest(Request request, Class<T> responseType) throws Exception {
        // Write request
        out.writeObject(request);
        out.flush();

        // read back whatever the server sent
        Object raw = in. readObject();
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


    public User userGetRequest(int id) throws IOException {
        Client client = new Client();

        try {
            client.startConnection("localhost", 4445);

            // build a request object
            Request req = new Request("User", id);

            return client.sendRequest(req, User.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                client.stopConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    public String oddsModifyRequest(int id) throws IOException {
        Client client = new Client();
        try {
            client.startConnection("localhost", 4444);
            System.out.println("Sending oddsModifyRequest with ID: " + id);
            return client.sendRequest(new Request("BaseBet", id), String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        stopConnection();
        return null;
    }


    /**
     * Retrieves an array of Game objects from the server.
     *
     * @return an array of Game objects.
     * @throws IOException if an I/O error occurs during retrieval.
     */
    private static Game[] getGames() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:9090/games"))
                .GET() // or .POST(HttpRequest.BodyPublishers.ofString("your JSON"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Status: " + response.statusCode());
//        System.out.println("Body: " + response.body());

        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = (JSONArray) jsonParser.parse(response.body());
        ArrayList<Game> allGames = new ArrayList<>();

//        System.out.println(jsonArray);
        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject) obj;
            System.out.println(jsonObject);
            Instant instant = Instant.parse(jsonObject.get("gameDate").toString());
            Date date = Date.from(instant);
            Game game = new Game((String) jsonObject.get("team1"), (String) jsonObject.get("team2"), (long) jsonObject.get("id"), date, (String) jsonObject.get("sport"), 0, 0);
            allGames.add(game);
        }

        return allGames.toArray(new Game[0]);
    }

    private static User getUser(int id) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:9090/user/0"))
                .GET() // or .POST(HttpRequest.BodyPublishers.ofString("your JSON"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Status: " + response.statusCode());
//        System.out.println("Body: " + response.body());

        JSONParser jsonParser = new JSONParser();
        JSONArray jsonArray = (JSONArray) jsonParser.parse(response.body());
        ArrayList<Game> allGames = new ArrayList<>();

//        System.out.println(jsonArray);
        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject) obj;
            System.out.println(jsonObject);
            Instant instant = Instant.parse(jsonObject.get("gameDate").toString());
            Date date = Date.from(instant);
            Game game = new Game((String) jsonObject.get("team1"), (String) jsonObject.get("team2"), (long) jsonObject.get("id"), date, (String) jsonObject.get("sport"), 0, 0);
            allGames.add(game);
        }


    }

    private static ArrayList<Game> getBasketballGames() throws Exception {
        ArrayList<Game> basketballGames;
        basketballGames = sendRequest(new Request("Basketball", 1), ArrayList.class);
        return basketballGames;
    }

    private static ArrayList<Game> getBaseballGames() throws Exception {
        ArrayList<Game> baseballGames;
        baseballGames = sendRequest(new Request("Baseball", 1), ArrayList.class);
        return baseballGames;
    }

    public static void main(String[] args) throws Exception {
        //        System.out.println(getSizeRequest(1));
        int port = 4444;
        System.out.println("Listening on port " + port);
        startConnection("localhost", port);
        // Test modification of user
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("Name", "John");
        attributes.put("Money", 9999);

        Game[] allGames = Client.getGames();

        new UI();
        UI.init(allGames, user);
//

    }


} //end class Client

