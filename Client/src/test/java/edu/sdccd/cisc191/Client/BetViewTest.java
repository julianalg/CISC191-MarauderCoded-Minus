package edu.sdccd.cisc191.Client;

import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;
import java.io.*;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import edu.sdccd.cisc191.Common.Models.Game;
import edu.sdccd.cisc191.Common.Models.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

class BetViewTest {

    @BeforeAll
    static void initJavaFX() {
        // This will initialize the JavaFX toolkit so all FX classes
        // can be used without "not on FX application thread" errors.
        new JFXPanel();
    }

    private Game makeGame(long id, String sport) {
        Game g = new Game();
        g.setId(id);
        g.setSport(sport);
        return g;
    }

    private User makeUser(int moneyBet) {
        User u = new User();
        u.setMoneyBet(moneyBet);
        return u;
    }

    @Test
    void grabOdds_returnsStubbedValue_whenClientSucceeds() throws Exception {
        Game game = makeGame(42L, "Basketball");
        BetView view = new BetView();
        view.game = game;

        try (MockedConstruction<Client> mc = Mockito.mockConstruction(Client.class, (mock, ctx) -> {
            Mockito.when(mock.getOdds(anyInt(), anyString(), anyInt()))
                    .thenReturn(5.5);
        })) {
            double odds = view.grabOdds(0);
            assertEquals(5.5, odds, 1e-9);
        }
    }

    @Test
    void grabOdds_returnsDefault2Point25_whenClientThrows() throws Exception {
        Game game = makeGame(7L, "Soccer");
        BetView view = new BetView();
        view.game = game;

        try (MockedConstruction<Client> mc = Mockito.mockConstruction(Client.class, (mock, ctx) -> {
            Mockito.when(mock.getOdds(anyInt(), anyString(), anyInt()))
                    .thenThrow(new IOException("network error"));
        })) {
            double odds = view.grabOdds(1);
            assertEquals(2.25, odds, 1e-9);
        }
    }

    @Test
    void betView_setsGameTeamAndUserFields() throws Exception {
        Game game = makeGame(99L, "Baseball");
        User user = makeUser(500);
        String team = "SomeTeam";

        class TestableBetView extends BetView {
            @Override
            public void start(Stage stage) {
                // do nothing
            }
            public Game getGameField() { return super.game; }
            public String getTeamField() { return super.team; }
            public User getUserField() { return super.user; }
        }

        TestableBetView view = new TestableBetView();
        Stage dummyStage = new Stage();
        view.betView(dummyStage, game, team, user);

        assertSame(game, view.getGameField());
        assertEquals(team, view.getTeamField());
        assertSame(user, view.getUserField());
    }
}
