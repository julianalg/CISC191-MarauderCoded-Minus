package edu.sdccd.cisc191.Server.API;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class BaseballGetter extends APIGetter {
    public BaseballGetter() {
        apiURL = "https://v1.baseball.api-sports.io/";
        leagueName = "MLB";
    }

    @Override
    public String sendRequest(URI requestURI) throws Exception {
        // Create an HttpClient instance
        HttpClient client = HttpClient.newHttpClient();
        String apiKey = System.getenv("API_KEY");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(requestURI)                  // <-- pass your concatenated URI here
                .header("x-rapidapi-host", "v1.baseball.api-sports.io")
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
