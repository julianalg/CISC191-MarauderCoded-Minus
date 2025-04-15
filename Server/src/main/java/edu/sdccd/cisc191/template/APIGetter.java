package edu.sdccd.cisc191.template;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class APIGetter {
    public APIGetter() {}

    public static ArrayList<Basketball> getBasketballGames() throws ParseException {
        // Create an HttpClient instance
        ArrayList<Basketball> bbGames = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        String apiKey = System.getenv("API_KEY");

        // Build the GET request with the required headers
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://v1.basketball.api-sports.io/games?date=2025-04-15"))
                .header("x-rapidapi-host", "v1.basketball.api-sports.io")
                .header("x-rapidapi-key", apiKey)
                .GET()
                .build();

        // Make an asynchronous request similar to using JavaScript promises
        String response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .exceptionally(e -> {
                    System.out.println("Error: " + e.getMessage());
                    return null;
                })
                .join().toString(); // Waits for the async call to complete

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response);

        for (Object keyObj : json.keySet()) {
            String key = (String) keyObj;
            Object value = json.get(key);

            System.out.println("Key: " + key + " - Value: " + value);

            // Optional: if the value is a nested JSON array or another JSONObject, you can iterate them too.
            if (value instanceof JSONArray) {
                JSONArray array = (JSONArray) value;
                for (Object item : array) {
                    // Here, you might need to cast item to a JSONObject if that's what it is.
                    if (item instanceof JSONObject) {
                        JSONObject nestedObj = (JSONObject) item;
                        // Process nestedObj here
                        JSONObject league = (JSONObject) nestedObj.get("league");
//                        System.out.println(league);
                        if (Objects.equals(league.get("name").toString(), "NBA")) {
                            JSONObject teams = (JSONObject) nestedObj.get("teams");
                            JSONObject awayTeam = (JSONObject) teams.get("away");
                            JSONObject homeTeam = (JSONObject) teams.get("home");
                            String awayTeamName = awayTeam.get("name").toString();
                            String homeTeamName = homeTeam.get("name").toString();

//                            System.out.println(awayTeamName + homeTeamName);

                            Basketball newGame = new Basketball(awayTeamName, homeTeamName);

                            bbGames.add(newGame);
                        }
                    }
                }
            }
        }

        System.out.println(bbGames);

//        List<Game> gdb = GameDatabase.getInstance().getGameDatabase();
//        gdb.addAll(bbGames);
//        System.out.println(gdb);

        return bbGames;
    }
}
