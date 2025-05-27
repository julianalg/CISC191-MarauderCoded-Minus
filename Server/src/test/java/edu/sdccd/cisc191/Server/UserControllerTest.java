package edu.sdccd.cisc191.Server;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.sdccd.cisc191.Common.IncomingBetDTO;
import edu.sdccd.cisc191.Common.Models.Game;
import edu.sdccd.cisc191.Common.Models.User;
import edu.sdccd.cisc191.Server.controllers.UserController;
import edu.sdccd.cisc191.Server.repositories.GameRepository;
import edu.sdccd.cisc191.Server.repositories.UserRepository;
import org.joda.time.DateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = UserController.class,
        excludeAutoConfiguration = {
                JpaRepositoriesAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class
        }
)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepo;

    @MockBean
    private GameRepository gameRepo;

    @Test
    @DisplayName("GET /users → 200")
    void whenGetAllUsers_then200() throws Exception {
        User u = new User(); u.setId(1L); u.setName("Alice"); u.setMoney(500);
        given(userRepo.findAll()).willReturn(Collections.singletonList(u));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Alice"));
    }

    @Test
    @DisplayName("DELETE /users/{id} → 200")
    void whenDelete_then200() throws Exception {
        willDoNothing().given(userRepo).deleteById(1L);
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /users/{id} → 200 & JSON user")
    void whenGetExistingUser_thenReturnUser() throws Exception {
        User u = new User();
        u.setId(2L);
        u.setName("Bob");
        u.setMoney(300);
        given(userRepo.findById(2L)).willReturn(Optional.of(u));

        mockMvc.perform(get("/users/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Bob"))
                .andExpect(jsonPath("$.money").value(300));
    }

    @Test
    @DisplayName("GET /users/{id} → 404 if not found")
    void whenGetMissingUser_then404() throws Exception {
        given(userRepo.findById(99L)).willReturn(Optional.empty());

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /users → create new user")
    void whenPostNewUser_thenCreateAndReturn() throws Exception {
        User request = new User();
        request.setName("Carol");
        request.setMoney(1000);

        User saved = new User();
        saved.setId(3L);
        saved.setName("Carol");
        saved.setMoney(1000);

        given(userRepo.save(any())).willReturn(saved);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Carol"))
                .andExpect(jsonPath("$.money").value(1000));
    }

    @Test
    @DisplayName("PUT /users/{id} → update existing user")
    void whenPutExistingUser_thenUpdateAndReturn() throws Exception {
        User existing = new User();
        existing.setId(4L);
        existing.setName("Dave");
        existing.setMoney(200);

        User changes = new User();
        changes.setName("David");
        changes.setMoney(250);

        given(userRepo.findById(4L)).willReturn(Optional.of(existing));
        given(userRepo.save(any())).willAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(put("/users/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changes)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.name").value("David"))
                .andExpect(jsonPath("$.money").value(250));
    }

    @Test
    @DisplayName("PUT /users/{id} → create if missing")
    void whenPutMissingUser_thenCreateNewWithGivenId() throws Exception {
        User newUser = new User();
        newUser.setName("Eve");
        newUser.setMoney(750);

        User saved = new User();
        saved.setId(5L);
        saved.setName("Eve");
        saved.setMoney(750);

        given(userRepo.findById(5L)).willReturn(Optional.empty());
        given(userRepo.save(any())).willReturn(saved);

        mockMvc.perform(put("/users/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("Eve"))
                .andExpect(jsonPath("$.money").value(750));
    }

    @Test
    @DisplayName("DELETE /users/{id} → 200 & remove user")
    void whenDeleteUser_then200() throws Exception {
        willDoNothing().given(userRepo).deleteById(6L);

        mockMvc.perform(delete("/users/6"))
                .andExpect(status().isOk());

        then(userRepo).should().deleteById(6L);
    }

    @Test
    @DisplayName("PATCH /{id}/bets → add a bet")
    void whenPatchAddBet_thenBetAdded() throws Exception {
        // prepare user & game
        User u = new User();
        u.setId(7L);
        u.setName("Frank");
        u.setMoney(400);
        u.setBets(new ArrayList<>());

        Game g = new Game();
        g.setId(10L);
        g.setSport("Baseball");
        g.setGameDate(DateTime.now().minusDays(1));

        given(userRepo.findById(7L)).willReturn(Optional.of(u));
        given(gameRepo.findById(10L)).willReturn(Optional.of(g));
        given(userRepo.save(any())).willAnswer(inv -> inv.getArgument(0));

        IncomingBetDTO dto = new IncomingBetDTO(10L, "HOME", 50, 80);

        mockMvc.perform(patch("/7/bets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bets", hasSize(1)))
                .andExpect(jsonPath("$.bets[0].betAmt").value(50))
                .andExpect(jsonPath("$.bets[0].betTeam").value("HOME"))
                .andExpect(jsonPath("$.bets[0].winAmt").value(80));
    }

    @Test
    @DisplayName("PATCH /{id}/bets → 404 if user missing")
    void whenPatchAddBet_userMissing_then404() throws Exception {
        given(userRepo.findById(8L)).willReturn(Optional.empty());

        String body = "{\"gameId\":1,\"betAmt\":10,\"betTeam\":\"X\",\"winAmt\":12}";

        mockMvc.perform(patch("/8/bets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /{id}/bets → 404 if game missing")
    void whenPatchAddBet_gameMissing_then404() throws Exception {
        User u = new User();
        u.setId(9L);
        given(userRepo.findById(9L)).willReturn(Optional.of(u));
        given(gameRepo.findById(2L)).willReturn(Optional.empty());

        String body = "{\"gameId\":2,\"betAmt\":10,\"betTeam\":\"Y\",\"winAmt\":15}";

        mockMvc.perform(patch("/9/bets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /updateAllBets → process and return message")
    void whenUpdateAllBets_thenReturnConfirmation() throws Exception {
        given(userRepo.findAll()).willReturn(Collections.emptyList());

        mockMvc.perform(get("/updateAllBets"))
                .andExpect(status().isOk())
                .andExpect(content().string("User bets have been updated"));
    }
}
