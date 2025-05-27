package edu.sdccd.cisc191.Client;

import edu.sdccd.cisc191.Common.Models.Game;
import edu.sdccd.cisc191.Common.Models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Timer;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BotBaseTest {
    @Mock
    private User mockUser;
    @Mock
    private Game mockGame;

    @BeforeEach
    void setupMocks() {
        when(mockUser.getId()).thenReturn(1L);
        when(mockUser.getName()).thenReturn("TestUser");

        when(mockGame.getId()).thenReturn(42L);
        when(mockGame.getDbId()).thenReturn(100L);
        when(mockGame.getTeam1()).thenReturn("TeamA");
        when(mockGame.getTeam2()).thenReturn("TeamB");
        when(mockGame.getSport()).thenReturn("Basketball");
    }

    @Test
    void getUser_returnsSameUser() throws Exception {
        try (MockedStatic<Client> mockedClient = mockStatic(Client.class)) {
            // Stub getGames to avoid NullPointerException in constructor
            mockedClient.when(Client::getGames).thenReturn(Collections.singletonList(mockGame));

            BotBase bot = new BotBase(mockUser);
            assertEquals(mockUser, bot.getUser(), "getUser should return the user passed to constructor");
        }
    }

    @Test
    void placeRandomBet_invokesPatchAddBetToMainUser() throws Exception {
        try (MockedStatic<Client> mockedClient = mockStatic(Client.class)) {
            // Stub getGames so constructor succeeds
            mockedClient.when(Client::getGames).thenReturn(Collections.singletonList(mockGame));
            // Stub getOdds to a fixed value
            mockedClient.when(() -> Client.getOdds(eq(42), eq("Basketball"), anyInt())).thenReturn(1.5);
            // No-op for patchAddBetToMainUser
            mockedClient.when(() -> Client.patchAddBetToMainUser(anyLong(), anyLong(), anyString(), anyInt(), anyInt()))
                    .then(invocation -> null);

            // Create bot instance
            BotBase bot = new BotBase(mockUser);

            // Reflectively call private placeRandomBet
            Method placeBet = BotBase.class.getDeclaredMethod("placeRandomBet", Game.class);
            placeBet.setAccessible(true);
            // Invoke
            placeBet.invoke(bot, mockGame);

            // Verify patchAddBetToMainUser was called once
            mockedClient.verify(() -> Client.patchAddBetToMainUser(eq(1L), eq(100L), anyString(), anyInt(), anyInt()), times(1));
        }
    }

    @Test
    void placeRandomBet_fallbackOnOddsException() throws Exception {
        try (MockedStatic<Client> mockedClient = mockStatic(Client.class)) {
            mockedClient.when(Client::getGames).thenReturn(Collections.singletonList(mockGame));
            // Stub getOdds to throw exception
            mockedClient.when(() -> Client.getOdds(eq(42), eq("Basketball"), anyInt()))
                    .thenThrow(new RuntimeException("API error"));
            mockedClient.when(() -> Client.patchAddBetToMainUser(anyLong(), anyLong(), anyString(), anyInt(), anyInt()))
                    .then(invocation -> null);

            BotBase bot = new BotBase(mockUser);
            Method placeBet = BotBase.class.getDeclaredMethod("placeRandomBet", Game.class);
            placeBet.setAccessible(true);
            placeBet.invoke(bot, mockGame);

            // Even when getOdds fails, patchAddBetToMainUser should still be called
            mockedClient.verify(() -> Client.patchAddBetToMainUser(eq(1L), eq(100L), anyString(), anyInt(), anyInt()), times(1));
        }
    }
}
