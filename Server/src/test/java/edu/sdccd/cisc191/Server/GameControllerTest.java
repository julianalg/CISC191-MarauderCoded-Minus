package edu.sdccd.cisc191.Server;

import edu.sdccd.cisc191.Common.Models.Game;
import edu.sdccd.cisc191.Server.API.BaseballGetter;
import edu.sdccd.cisc191.Server.API.BasketballGetter;
import edu.sdccd.cisc191.Server.repositories.GameRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.joda.time.DateTime;

@SpringBootTest
@AutoConfigureMockMvc
class GameControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameRepositoryImpl gameRepository;

    @Autowired
    private BaseballGetter baseballGetter;

    @Autowired
    private BasketballGetter basketballGetter;

    private Game testGame;
    private List<Game> testGames;

    @BeforeEach
    void setUp() {
        testGame = new Game("Team1", "Team2", 1L, new Date(), "Baseball", 2.0, 1.5);
        testGames = Arrays.asList(
            testGame,
            new Game("Team3", "Team4", 2L, new Date(), "Basketball", 1.8, 2.2)
        );
    }

    @Test
    void getAllGames() throws Exception {
        when(gameRepository.findAll()).thenReturn(testGames);

        mockMvc.perform(get("/games"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].team1").value("Team1"));
    }

    @Test
    void getGameById() throws Exception {
        when(gameRepository.findByIdUsingBST(1L)).thenReturn(Optional.of(testGame));

        mockMvc.perform(get("/games/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.team1").value("Team1"));
    }

    @Test
    void getGameByIdNotFound() throws Exception {
        when(gameRepository.findByIdUsingBST(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/games/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void createGame() throws Exception {
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);

        mockMvc.perform(post("/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Game.toJSON(testGame)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.team1").value("Team1"));
    }

    @Test
    void getGamesByDateRange() throws Exception {
        DateTime start = new DateTime();
        DateTime end = start.plusDays(7);
        
        when(gameRepository.findAll()).thenReturn(testGames);

        mockMvc.perform(get("/games/dateRange")
                .param("startDate", start.toString())
                .param("endDate", end.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getGamesBySport() throws Exception {
        when(gameRepository.findAll()).thenReturn(testGames);

        mockMvc.perform(get("/games/sport/Baseball"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].sport").value("Baseball"));
    }
}