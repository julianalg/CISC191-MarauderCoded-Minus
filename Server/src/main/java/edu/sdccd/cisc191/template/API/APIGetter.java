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


    public ArrayList<Game> getGames() throws ParseException {
        // Create an HttpClient instance
        ArrayList<Game> bbGames = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        String apiKey = System.getenv("API_KEY");

        Date today = new Date(); // current date
        LocalDate localDate = today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        LocalDate nextDay = localDate.plusDays(1);
        Date nextDayDate = Date.from(nextDay.atStartOfDay(ZoneId.systemDefault()).toInstant());

        LocalDate tomorrowLocalDate = nextDayDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        String tomorrowArg = tomorrowLocalDate.getYear() + "-0" + tomorrowLocalDate.getMonthValue() + "-" + tomorrowLocalDate.getDayOfMonth();
        System.out.println(tomorrowArg);

        String fullUrl     = apiURL + tomorrowArg;
        URI   requestURI  = URI.create(fullUrl);
        System.out.println(requestURI);  // prints: https://v1.basketball.api‑sports.io/games?date=2025‑04‑22

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

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response);

        System.out.println(leagueName);

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
                        if (Objects.equals(league.get("name").toString(), leagueName)) {
                            JSONObject teams = (JSONObject) nestedObj.get("teams");
                            JSONObject awayTeam = (JSONObject) teams.get("away");
                            JSONObject homeTeam = (JSONObject) teams.get("home");
                            String awayTeamName = awayTeam.get("name").toString();
                            String homeTeamName = homeTeam.get("name").toString();

                            System.out.println(awayTeamName + homeTeamName);

                            Game newGame = new Game(awayTeamName, homeTeamName, new Date(), new Date());

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
