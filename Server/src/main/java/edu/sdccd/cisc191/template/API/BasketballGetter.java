package edu.sdccd.cisc191.template.API;

import edu.sdccd.cisc191.template.Game;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class BasketballGetter extends APIGetter {
    public BasketballGetter() {
        apiURL = "https://v1.basketball.api-sports.io/games?date=";
        leagueName = "NBA";
    }

    public static void main(String[] args) throws Exception {
        BasketballGetter basketballGetter = new BasketballGetter();
        System.out.println(basketballGetter.getGames("Basketball"));
    }

    @Override
    public String sendRequest(URI requestURI) throws Exception {
        // Create an HttpClient instance
        HttpClient client = HttpClient.newHttpClient();
        String apiKey = System.getenv("API_KEY");

        ArrayList<Game> bbGames = new ArrayList<>();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(requestURI)                  // <-- pass your concatenated URI here
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

        return response;
    }

}
