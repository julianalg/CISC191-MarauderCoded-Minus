package edu.sdccd.cisc191.Server.API;

import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Objects;

import edu.sdccd.cisc191.Common.Models.Game;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Abstract class responsible for retrieving game and betting data from an external sports API.
 * This class provides methods for sending HTTP requests, parsing game and odds data,
 * and retrieving betting odds and game results.
 */
public abstract class APIGetter {
    protected String apiURL;
    protected String leagueName;

    /**
     * Default constructor.
     */
    public APIGetter() {}

    /**
     * Gets the date string formatted as yyyy-MM-dd for tomorrow’s date.
     *
     * @return formatted string representing tomorrow's date
     */
    public String getDateAsString() {
        Date today = new Date();
        LocalDate localDate = today.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate nextDay = localDate.plusDays(1);
        Date nextDayDate = Date.from(nextDay.atStartOfDay(ZoneId.systemDefault()).toInstant());
        LocalDate tomorrowLocalDate = nextDayDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (tomorrowLocalDate.getDayOfMonth() < 10) {
            return tomorrowLocalDate.getYear() + "-0" + tomorrowLocalDate.getMonthValue() + "-0" + tomorrowLocalDate.getDayOfMonth();
        } else {
            return tomorrowLocalDate.getYear() + "-0" + tomorrowLocalDate.getMonthValue() + "-" + tomorrowLocalDate.getDayOfMonth();
        }
    }

    /**
     * Retrieves a list of {@link Game} objects for the given sport based on the next day's schedule.
     *
     * @param sport the sport type (e.g., "Baseball", "Basketball")
     * @return a list of games for the specified sport
     * @throws Exception if the API request or parsing fails
     */
    public ArrayList<Game> getGames(String sport) throws Exception {
        String fullUrl = apiURL + "games?date=" + getDateAsString();
        URI requestURI = URI.create(fullUrl);
        String response = sendRequest(requestURI);
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response);
        return parse(json, sport);
    }

    /**
     * Sends an HTTP GET request to the given URI and returns the response body.
     *
     * @param requestURI the target URI for the GET request
     * @return the response body as a String
     * @throws Exception if the request fails
     */
    public String sendRequest(URI requestURI) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String apiKey = System.getenv("API_KEY");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(requestURI)
                .header("x-rapidapi-host", "v1.baseball.api-sports.io")
                .header("x-rapidapi-key", apiKey)
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .exceptionally(e -> {
                    System.out.println("Error: " + e.getMessage());
                    return null;
                })
                .join()
                .toString();
    }

    /**
     * Parses a JSON object and extracts a list of {@link Game} objects for the given sport.
     *
     * @param json the root JSON object from the API
     * @param sport the sport type to filter games by
     * @return a list of parsed games
     * @throws ParseException if parsing fails
     */
    public ArrayList<Game> parse(JSONObject json, String sport) throws ParseException {
        ArrayList<Game> games = new ArrayList<>();

        for (Object keyObj : json.keySet()) {
            String key = (String) keyObj;
            Object value = json.get(key);

            if (value instanceof JSONArray) {
                JSONArray array = (JSONArray) value;
                for (Object item : array) {
                    if (item instanceof JSONObject) {
                        JSONObject nestedObj = (JSONObject) item;
                        JSONObject league = (JSONObject) nestedObj.get("league");
                        int gameID = Integer.parseInt(nestedObj.get("id").toString());

                        if (Objects.equals(league.get("name").toString(), leagueName)) {
                            JSONObject teams = (JSONObject) nestedObj.get("teams");
                            JSONObject awayTeam = (JSONObject) teams.get("away");
                            JSONObject homeTeam = (JSONObject) teams.get("home");

                            String awayTeamName = awayTeam.get("name").toString();
                            String homeTeamName = homeTeam.get("name").toString();
                            String date = nestedObj.get("date").toString();

                            OffsetDateTime odt = OffsetDateTime.parse(date);
                            Instant instant = odt.toInstant();
                            Date legacyDate = Date.from(instant);

                            Game newGame = new Game(awayTeamName, homeTeamName, gameID, legacyDate, sport, 0, 0);
                            games.add(newGame);
                        }
                    }
                }
            }
        }

        return games;
    }

    /**
     * Fetches the betting odds string for a given game ID.
     *
     * @param gameId the unique identifier of the game
     * @return the odds information as a string
     * @throws ParseException if JSON parsing fails
     */
    public String getOdd(long gameId) throws ParseException {
        HttpClient client = HttpClient.newHttpClient();
        String apiKey = System.getenv("API_KEY");
        URI betURI = URI.create(apiURL + "odds?game=" + gameId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(betURI)
                .header("x-rapidapi-host", apiURL)
                .header("x-rapidapi-key", apiKey)
                .GET()
                .build();

        String response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .exceptionally(e -> {
                    System.out.println("Error: " + e.getMessage());
                    return null;
                })
                .join()
                .toString();

        return parseBet(response);
    }

    /**
     * Parses the JSON response from the odds API and extracts bookmaker information.
     *
     * @param response the raw JSON string response
     * @return a string representation of the bookmaker data
     * @throws ParseException if parsing fails
     */
    public String parseBet(String response) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(response);
        JSONArray jsonResponse = (JSONArray) json.get("response");
        JSONObject gameObj = (JSONObject) jsonResponse.getFirst();
        return gameObj.get("bookmakers").toString();
    }

    /**
     * Retrieves the winning team name for a completed game by its ID.
     *
     * @param id the game ID
     * @return the name of the winning team or "Drop" if the result is unavailable
     * @throws ParseException if parsing fails
     */
    public String getWinner(long id) throws ParseException {
        HttpClient client = HttpClient.newHttpClient();
        String apiKey = System.getenv("API_KEY");
        URI betURI = URI.create(apiURL + "/games?id=" + id);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(betURI)
                .header("x-rapidapi-host", apiURL)
                .header("x-rapidapi-key", apiKey)
                .GET()
                .build();

        String response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .exceptionally(e -> {
                    System.out.println("Error: " + e.getMessage());
                    return null;
                })
                .join()
                .toString();

        try {
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(response);
            JSONArray jsonResponse = (JSONArray) obj.get("response");
            JSONObject gameObj = (JSONObject) jsonResponse.getFirst();
            JSONObject teams = (JSONObject) gameObj.get("teams");
            JSONObject awayTeam = (JSONObject) teams.get("away");
            JSONObject homeTeam = (JSONObject) teams.get("home");
            String homeTeamName = homeTeam.get("name").toString();
            String awayTeamName = awayTeam.get("name").toString();

            JSONObject scores = (JSONObject) gameObj.get("scores");
            int awayTeamScore = Integer.parseInt(((JSONObject) scores.get("away")).get("total").toString());
            int homeTeamScore = Integer.parseInt(((JSONObject) scores.get("home")).get("total").toString());

            return awayTeamScore > homeTeamScore ? awayTeamName : homeTeamName;
        } catch (IndexOutOfBoundsException | NoSuchElementException e) {
            System.out.println("IndexOutOfBoundsException, presumably game is outside API call radius");
            return "Drop";
        }
    }
}
