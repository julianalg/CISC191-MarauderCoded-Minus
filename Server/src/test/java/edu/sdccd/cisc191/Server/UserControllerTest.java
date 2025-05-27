package edu.sdccd.cisc191.Server;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.sdccd.cisc191.Common.Models.Bet;
import edu.sdccd.cisc191.Common.Models.Game;
import edu.sdccd.cisc191.Common.Models.User;
import edu.sdccd.cisc191.Common.IncomingBetDTO;
import edu.sdccd.cisc191.Server.repositories.GameRepository;
import edu.sdccd.cisc191.Server.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private GameRepository gameRepository;

    private User testUser;
    private Game testGame;
    private Bet testBet;

    @BeforeEach
    void setUp() {
        testUser = new User("TestUser", 1000);
        testGame = new Game("Team1", "Team2", 1L, new Date(), "Baseball", 2.0, 1.5);
        testBet = new Bet(testGame, 100, "Team1", 200);
    }

    @Test
    void getAllUsers() throws Exception {
        List<User> users = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(users);

        mockMvc.perform(get("/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name").value("TestUser"));
    }

    @Test
    void createUser() throws Exception {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(User.toJSON(testUser)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("TestUser"));
    }

    @Test
    void addBet() throws Exception {
        testUser.addBet(testBet);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        IncomingBetDTO dto = new IncomingBetDTO(1L, "Team1", 100, 200);

        mockMvc.perform(patch("/1/bets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.bets", hasSize(1)));
    }

    @Test
    void updateAllBets() throws Exception {
        testUser.addBet(testBet);
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        mockMvc.perform(get("/updateAllBets"))
                .andExpect(status().isOk())
                .andExpect(content().string("Bad things have happened here"));
    }

}