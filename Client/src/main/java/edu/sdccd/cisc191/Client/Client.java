package edu.sdccd.cisc191.Client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.sdccd.cisc191.Common.Models.Bet;
import edu.sdccd.cisc191.Common.Models.Game;
import edu.sdccd.cisc191.Common.Models.User;
import edu.sdccd.cisc191.Common.Request;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.Getter;
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
import java.util.*;

/**
 * The Client class establishes a connection to a server, sends requests, and builds the JavaFX UI for the application.
 * It handles game, user, and size requests, as well as modifications to user data.
 */
public class Client {

    protected HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }

    /**
     * Grabs odds from the Sports API via the Database Server
     *
     * @param gameId the game ID (from the API) to grab the odds for.
     * @param sport the sport to grab the odds for.
     * @param homeOrAway 0 for home, 1 for away.
     * */
    public static double getOdds(int gameId, String sport, int homeOrAway) throws IOException, InterruptedException {
        HttpClient client = new Client().httpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:9090/games/odds/" + sport + "/" + gameId))
                .GET() // or .POST(HttpRequest.BodyPublishers.ofString("your JSON"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Status: " + response.statusCode());
//        System.out.println("Body: " + response.body());

        String json = response.body();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        double homeOddStat = 2.25;
        double awayOddStat = 2.25;

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

        if (homeOrAway == 0) {
            return homeOddStat;
        } else {
            return awayOddStat;
        }

    }

    /**
     * Retrieves an array of Game objects from the server.
     *
     * @return an array of Game objects.
     * @throws IOException if an I/O error occurs during retrieval.
     */
    public static ArrayList<Game> getGames() throws Exception {
        HttpClient client = new Client().httpClient();

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
//            System.out.println(jsonObject);
            Instant instant = Instant.parse(jsonObject.get("gameDate").toString());
            Date date = Date.from(instant);
            Game game = new Game((String) jsonObject.get("team1"), (String) jsonObject.get("team2"), (long) jsonObject.get("id"), date, (String) jsonObject.get("sport"), (long) jsonObject.get("dbId"));
            allGames.add(game);
        }

        return allGames;
    }

    /**
     * Retrieves the primary user, user of ID 1, from the server and initializes it to a User object
     * */
    public static User getMainUser() throws Exception {
        HttpClient client = new Client().httpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:9090/users/1"))
                .GET() // or .POST(HttpRequest.BodyPublishers.ofString("your JSON"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Status: " + response.statusCode());
        System.out.println("Body: " + response.body());

        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(response.body());
        JSONArray bets = (JSONArray) jsonObject.get("bets");
        User mainUser = new User((String) jsonObject.get("name"), (long) jsonObject.get("money"));
        for (Object obj : bets) {
            JSONObject bet = (JSONObject) obj;
            JSONObject game = (JSONObject) bet.get("game");
//            System.out.println(game);
            String iso = game.get("gameDate").toString();
            Instant instant = Instant.parse(iso);
            Date date = Date.from(instant);
            Game betGame = new Game((String) game.get("team1"), (String) game.get("team2"), (Long) game.get("id"), date, (String) game.get("sport"), (Long) game.get("dbId"));
            Bet newBet = new Bet(betGame, Math.toIntExact((Long) bet.get("betAmt")), (String) bet.get("betTeam"), Math.toIntExact((Long) bet.get("winAmt")));
            mainUser.addBet(newBet);
            System.out.println("Bet: " + newBet);
        }

        return mainUser;

    }

    /**
     * Uses a PUT request to update the main user's money amount.
     * @param money the new amount of money to set the main user's money to.
     * */
    private static void updateMainUserMoney(long money) throws Exception {
        HttpClient client = new Client().httpClient();

        String jsonBody = "{\"id\": 1, \"name\": \"Chase\", \"money\": " + money + "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:9090/users/1"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Status Code: " + response.statusCode());
        System.out.println("Response Body: " + response.body());

    }

    /**
     * Calls a GET method on the Database server to fulfill expired user bets
     * */
    public static void updateBets() throws Exception {
        HttpClient client = new Client().httpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:9090/updateAllBets"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Status Code (update bets on app open): " + response.statusCode());
    }

    /**
     * Calls a PATCH method on the Database server to add a bet to a user's bets.
     * @param userId the ID of the user to add the bet to.
     * @param gameId the ID (from the API) of the game to add the bet for.
     * @param betTeam the team the user bets on.
     * @param betAmt the amount the user bets.
     * @param winAmt the amount the user wins.
     * */
    public static void patchAddBetToMainUser(Long userId, Long gameId, String betTeam, int betAmt, int winAmt) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        // build only the DTO fields
        Map<String,Object> dto = Map.of(
                "gameId", gameId,
                "betTeam", betTeam,
                "betAmt", betAmt,
                "winAmt", winAmt
        );
        String jsonBody = mapper.writeValueAsString(dto);

        System.out.println("PATCH body: " + jsonBody);

        HttpClient client = new Client().httpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:9090/" + userId + "/bets"))
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Status Code: " + response.statusCode());
        System.out.println("Response Body: " + response.body());
    }

    @Getter
    private static ArrayList<BotBase> bots = new ArrayList<>();

    /**
     * Creates an array of BotBase objects from the server.
     * @throws Exception if an error occurs during retrieval.
     * */
    public static void createBotArray() throws Exception {
        HttpClient client = new Client().httpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:9090/users"))
                .GET() // or .POST(HttpRequest.BodyPublishers.ofString("your JSON"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Status: " + response.statusCode());
        System.out.println("Body: " + response.body());

        ObjectMapper mapper = new ObjectMapper()
                // ignore any JSON properties you don’t have in your class
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // readValue will call your no-arg constructor and setters (or @JsonCreator)
        List<User> botUsers = mapper.readValue(response.body(), new TypeReference<List<User>>() {});
        botUsers.remove(botUsers.getFirst());

        for (User user : botUsers) {
            BotBase newBot = new BotBase(user);
            bots.add(newBot);
        }
    }

    /**
     * Starts all the bots in the array.
     * @throws Exception if an error occurs during retrieval.
     */
    public static void launchBots() throws Exception {
        createBotArray();
        for (BotBase bot : bots) {
            bot.startBot();
        }
    }

    /**
     * Launches bots and the JavaFX UI
     * @param args the command line arguments.
     * @throws Exception if an error occurs during retrieval.
     * */
    public static void main(String[] args) throws Exception {
        launchBots();
        Application.launch(UI.class, args);
    }


} //end class Client

