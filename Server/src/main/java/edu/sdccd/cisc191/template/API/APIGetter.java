package edu.sdccd.cisc191.template.API;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import edu.sdccd.cisc191.template.Game;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public abstract class APIGetter {
     String apiURL;
     String leagueName;
    public APIGetter() {}

    public String getDateAsString() {
        Date today = new Date(); // current date
        LocalDate localDate = today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        LocalDate nextDay = localDate.plusDays(1);
        Date nextDayDate = Date.from(nextDay.atStartOfDay(ZoneId.systemDefault()).toInstant());

        LocalDate tomorrowLocalDate = nextDayDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        String tomorrowArg = tomorrowLocalDate.getYear() + "-0" + tomorrowLocalDate.getMonthValue() + "-0" + tomorrowLocalDate.getDayOfMonth();
        return tomorrowArg;
    }

    public ArrayList<Game> getGames() throws Exception {
        String fullUrl     = apiURL + "games?date=" + getDateAsString();
        URI   requestURI  = URI.create(fullUrl);
        System.out.println(requestURI);

        String response = sendRequest(requestURI);

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response);


        ArrayList<Game> games = parse(json);

        return games;
    }

    public abstract String sendRequest(URI requestURI) throws Exception;

    public ArrayList<Game> parse(JSONObject json) throws ParseException {
        ArrayList<Game> games = new ArrayList<>();

        for (Object keyObj : json.keySet()) {
            String key = (String) keyObj;
            Object value = json.get(key);

            long gameId;

            System.out.println("Key: " + key + " - Value: " + value);

            // Optional: if the value is a nested JSON array or another JSONObject, you can iterate them too.
            if (value instanceof JSONArray) {
                JSONArray array = (JSONArray) value;
                for (Object item : array) {
                    if (item instanceof JSONObject) {
                        JSONObject nestedObj = (JSONObject) item;
                        JSONObject league = (JSONObject) nestedObj.get("league");
                        if (Objects.equals(league.get("name").toString(), leagueName)) {
                            gameId = (long) nestedObj.get("id");
                            System.out.println(gameId);
                            JSONObject teams = (JSONObject) nestedObj.get("teams");
                            JSONObject awayTeam = (JSONObject) teams.get("away");
                            JSONObject homeTeam = (JSONObject) teams.get("home");
                            String awayTeamName = awayTeam.get("name").toString();
                            String homeTeamName = homeTeam.get("name").toString();

                            System.out.println(awayTeamName + homeTeamName);

                            Game newGame = new Game(awayTeamName, homeTeamName, new Date(), 0, 0);

                            games.add(newGame);

                            getOdd(0, gameId);
                        }
                    }
                }
            }
        }
        return games;
    }

    public float getOdd(int team, long gameId) {
        // Create an HttpClient instance
        HttpClient client = HttpClient.newHttpClient();
        String apiKey = System.getenv("API_KEY");
        URI betURI = URI.create(apiURL + "odds?game=" + gameId + "&bookmaker=5");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(betURI)                  // <-- pass your concatenated URI here
                .header("x-rapidapi-host", apiURL)
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

        System.out.println(response);
        return 0;
    }
};
