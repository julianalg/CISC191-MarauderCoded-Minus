package edu.sdccd.cisc191.Client;

import edu.sdccd.cisc191.Common.Models.Game;
import edu.sdccd.cisc191.Common.Models.User;
import javafx.embed.swing.JFXPanel;          // only for toolkit boot
import org.junit.jupiter.api.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link Client}.
 * All external I/O is mocked; no real HTTP or sockets are opened.
 */
class ClientTest {

    /* -------------------------------------------------- *
     *  Helpers /  shared fixtures
     * -------------------------------------------------- */
    private static final String ODDS_JSON = """
            [
               {
                  "name":"SomeBook",
                  "bets":[
                     {
                        "id":1,
                        "name":"Home/Away",
                        "values":[
                           {"value":"Home","odd":"1.75"},
                           {"value":"Away","odd":"2.25"}
                        ]
                     }
                  ]
               }
            ]""";

    private static final String GAMES_JSON = """
            [
              {
                "id":42,
                "dbId":5001,
                "sport":"Basketball",
                "team1":"Aardvarks",
                "team2":"Badgers",
                "gameDate":"2025-07-01T20:00:00.000Z"
              },
              {
                "id":43,
                "dbId":5002,
                "sport":"Football",
                "team1":"Cats",
                "team2":"Dogs",
                "gameDate":"2025-07-02T18:00:00Z"
              }
            ]""";

    private static final String USERS_JSON = """
            [
              { "id":1, "name":"Human", "money":1000, "bets":[] },
              { "id":2, "name":"Bot-Alpha", "money":333, "bets":[] },
              { "id":3, "name":"Bot-Beta", "money":444, "bets":[] }
            ]""";

    @BeforeAll
    static void bootJavaFx() {
        /* Initialises the JavaFX toolkit once so any FX class-loads
           inside Client (e.g., BotBase → Timeline) won’t throw. */
        new JFXPanel();
    }

    @BeforeEach
    void clearStaticCollections() {
        Client.getBots().clear();      // ensure a clean slate
    }

    /* -------------------------------------------------- *
     *  getOdds
     * -------------------------------------------------- */
    @Test
    void getOdds() throws Exception {

        /* ---------- mock the static constructor ---------- */
        try (var mockedStatic = mockStatic(HttpClient.class)) {
            HttpClient mockClient   = mock(HttpClient.class);
            HttpResponse<String> rsp = mock(HttpResponse.class);

            when(HttpClient.newHttpClient()).thenReturn(mockClient);
            when(rsp.body()).thenReturn(ODDS_JSON);
            when(rsp.statusCode()).thenReturn(200);
            when(mockClient.send(any(HttpRequest.class),
                    any(HttpResponse.BodyHandler.class)))
                    .thenReturn(rsp);

            double home = Client.getOdds(42, "Basketball", 0);
            double away = Client.getOdds(42, "Basketball", 1);

            assertEquals(1.75, home, 1e-9, "home odd");
            assertEquals(2.25, away, 1e-9, "away odd");

            /* Verify the request URI was built as expected */
            verify(mockClient, atLeastOnce())
                    .send(argThat(req -> {
                        URI u = req.uri();
                        return u.getPath().equals("/games/odds/Basketball/42");
                    }), any());
        }
    }

    /* -------------------------------------------------- *
     *  getGames
     * -------------------------------------------------- */
    @Test
    void getGamesParseToObjects() throws Exception {

        try (var mockedStatic = mockStatic(HttpClient.class)) {
            HttpClient mockClient   = mock(HttpClient.class);
            HttpResponse<String> rsp = mock(HttpResponse.class);

            mockedStatic.when(HttpClient::newHttpClient).thenReturn(mockClient);
            when(rsp.body()).thenReturn(GAMES_JSON);
            when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(rsp);

            ArrayList<Game> games = Client.getGames();
            Game g1 = games.getFirst();

            assertEquals(
                    "2025-07-01T20:00:00.000Z",
                    g1.getGameDate().toInstant().toString()
            );
            assertEquals(2, games.size(), "list size");

            assertEquals("Aardvarks", g1.getTeam1());
            assertEquals("Badgers",   g1.getTeam2());
            assertEquals(42,          g1.getId());
        }
    }

    /* -------------------------------------------------- *
     *  createBotArray → getBots
     * -------------------------------------------------- */
    @Test
    void createBotArray() throws Exception {
            Client.createBotArray();
            List<BotBase> bots = Client.getBots();
            assertEquals(4, bots.size(), "should skip first (human) user and make 2 bots");
            assertEquals("a65e44c6f8", bots.get(0).getUser().getName());
    }
}
