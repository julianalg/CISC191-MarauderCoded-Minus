package edu.sdccd.cisc191.Server;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.boot.test.web.client.TestRestTemplate;

import edu.sdccd.cisc191.Common.Models.User;
import edu.sdccd.cisc191.Server.repositories.UserRepository;
import edu.sdccd.cisc191.Server.repositories.GameRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean(name = "userRepository")
    private UserRepository userRepository;

    @MockBean(name = "gameRepository")
    private GameRepository gameRepository;

    @Test
    void deleteUserThen200() {
        willDoNothing().given(userRepository).deleteById(1L);

        ResponseEntity<Void> response = restTemplate.exchange(
            "/users/1", HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getExistingUserThenReturn200() throws Exception {
        User u = new User();
        u.setId(2L);
        u.setName("Bob");
        u.setMoney(300);
        given(userRepository.findById(2L)).willReturn(Optional.of(u));

        ResponseEntity<User> response = restTemplate.getForEntity(
            "/users/2", User.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2L, response.getBody().getId());
        assertEquals("Bob", response.getBody().getName());
        assertEquals(300, response.getBody().getMoney());
    }

    @Test
    void getMissingUserThen404() {
        given(userRepository.findById(99L)).willReturn(Optional.empty());

        ResponseEntity<User> response = restTemplate.exchange(
            "/users/99", HttpMethod.GET, null, User.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void postNewUserThenCreateAndReturn200() {
        User request = new User();
        request.setName("Carol");
        request.setMoney(1000);

        User saved = new User();
        saved.setId(3L);
        saved.setName("Carol");
        saved.setMoney(1000);

        given(userRepository.save(any(User.class))).willReturn(saved);

        ResponseEntity<User> response = restTemplate.postForEntity(
            "/users", request, User.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3L, response.getBody().getId());
        assertEquals("Carol", response.getBody().getName());
        assertEquals(1000, response.getBody().getMoney());
    }

    @Test
    void putExistingUserThenUpdateAndReturn200() throws Exception {
        User existing = new User();
        existing.setId(4L);
        existing.setName("Dave");
        existing.setMoney(200);

        User changes = new User();
        changes.setName("David");
        changes.setMoney(250);

        given(userRepository.findById(4L)).willReturn(Optional.of(existing));
        given(userRepository.save(any(User.class)))
            .willAnswer(inv -> {
                User u = inv.getArgument(0);
                u.setId(4L);
                return u;
            });

        HttpEntity<User> entity = new HttpEntity<>(changes);
        ResponseEntity<User> response = restTemplate.exchange(
            "/users/4", HttpMethod.PUT, entity, User.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(4L, response.getBody().getId());
        assertEquals("David", response.getBody().getName());
        assertEquals(250, response.getBody().getMoney());
    }

    @Test
    void putMissingUserThenCreateAndReturn() {
        User newUser = new User();
        newUser.setName("Eve");
        newUser.setMoney(750);

        User saved = new User();
        saved.setId(5L);
        saved.setName("Eve");
        saved.setMoney(750);

        given(userRepository.findById(5L)).willReturn(Optional.empty());
        given(userRepository.save(any(User.class))).willReturn(saved);

        HttpEntity<User> entity = new HttpEntity<>(newUser);
        ResponseEntity<User> response = restTemplate.exchange(
            "/users/5", HttpMethod.PUT, entity, User.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(5L, response.getBody().getId());
        assertEquals("Eve", response.getBody().getName());
        assertEquals(750, response.getBody().getMoney());
    }

    @Test
    void deleteUserthenDeleteAndReturn200() throws Exception {
        willDoNothing().given(userRepository).deleteById(6L);

        ResponseEntity<Void> response = restTemplate.exchange(
            "/users/6", HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        then(userRepository).should().deleteById(6L);
    }


}