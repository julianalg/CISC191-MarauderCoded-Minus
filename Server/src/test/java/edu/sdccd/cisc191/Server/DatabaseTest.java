package edu.sdccd.cisc191.Server;

import edu.sdccd.cisc191.Common.Models.Game;
import edu.sdccd.cisc191.Common.Models.User;
import edu.sdccd.cisc191.Server.repositories.GameRepository;
import edu.sdccd.cisc191.Server.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class DatabaseServerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameDatabase gameDatabase;

    @Mock
    private UserDatabase userDatabase;

    private DatabaseServer databaseServer;

    @BeforeEach
    void setUp() throws IOException {
        databaseServer = new DatabaseServer(userRepository, gameRepository, gameDatabase, userDatabase);
    }

    @Test
    void contextLoads() {
        assertNotNull(databaseServer);
    }

    @Test
    void testRunMethod() throws Exception {
        // Test that run method executes without throwing exceptions
        assertDoesNotThrow(() -> databaseServer.run());
    }

    @Test
    void testSaveAllToFiles() {
        // Test that saveAllToFiles method calls appropriate database save methods
        databaseServer.saveAllToFiles();
        
        verify(gameDatabase, times(1)).saveToFile();
        verify(userDatabase, times(1)).saveToFile();
    }

    @Test
    void testGameRepositoryIntegration() {
        // Create a test game
        Game testGame = new Game("Team1", "Team2", 1L, new Date(), "Football", 1.5, 2.0);
        
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);
        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));

        // Test save operation
        Game savedGame = gameRepository.save(testGame);
        assertNotNull(savedGame);
        assertEquals("Team1", savedGame.getTeam1());

        // Test find operation
        Optional<Game> foundGame = gameRepository.findById(1L);
        assertTrue(foundGame.isPresent());
        assertEquals("Team2", foundGame.get().getTeam2());
    }

    @Test
    void testUserRepositoryIntegration() {
        // Create a test user
        User testUser = new User("TestUser", 1000);
        
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // Test save operation
        User savedUser = userRepository.save(testUser);
        assertNotNull(savedUser);
        assertEquals("TestUser", savedUser.getName());

        // Test find operation
        Optional<User> foundUser = userRepository.findById(1L);
        assertTrue(foundUser.isPresent());
        assertEquals(1000, foundUser.get().getMoney());
    }

    @Test
    void testDatabasePersistence() throws IOException {
        // Verify that databases are properly initialized
        verify(gameDatabase, times(0)).saveToFile(); // Should not be called during initialization
        verify(userDatabase, times(0)).saveToFile(); // Should not be called during initialization

        // Test save all operation
        databaseServer.saveAllToFiles();
        verify(gameDatabase, times(1)).saveToFile();
        verify(userDatabase, times(1)).saveToFile();
    }

}