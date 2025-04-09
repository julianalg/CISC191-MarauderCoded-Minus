package edu.sdccd.cisc191.template;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
public class APIGetter {
    public static void main(String[] args) {
        // Create an HttpClient instance
        HttpClient client = HttpClient.newHttpClient();

        // Build the GET request with the required headers
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://v1.basketball.api-sports.io/games?date=2025-04-10"))
                .header("x-rapidapi-host", "v1.basketball.api-sports.io")
                .header("x-rapidapi-key", "8071e311490d3c0720637fdfd88992e3")
                .GET()
                .build();

        // Make an asynchronous request similar to using JavaScript promises
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(System.out::println)
                .exceptionally(e -> {
                    System.out.println("Error: " + e.getMessage());
                    return null;
                })
                .join(); // Waits for the async call to complete
    }
}
