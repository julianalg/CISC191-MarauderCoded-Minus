package edu.sdccd.cisc191.template;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ClientTest {

    private UI UI;
    private Stage primaryStage;
    private Scene scene;

    @BeforeAll
    public static void initJFX() throws Exception {
        if (!Platform.isFxApplicationThread()) {
            final CountDownLatch latch = new CountDownLatch(1);
            // Initialize the JavaFX Platform
            Platform.startup(() -> {
                // No need to do anything here
                latch.countDown();
            });
            if (!latch.await(5, TimeUnit.SECONDS)) {
                throw new Exception("JavaFX platform failed to start.");
            }
        }
    }

    private void setupStageAndScene() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                primaryStage = new Stage();
                UI = new UI();
                UI.start(primaryStage);
                primaryStage.show();
                scene = primaryStage.getScene();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new Exception("Setup did not complete in time.");
        }
    }

    @Test
    public void testStageTitle() throws Exception {
        setupStageAndScene();
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            assertEquals("Marauder Bets", primaryStage.getTitle(), "Stage title should be 'Marauder Bets'.");
            latch.countDown();
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("testStageTitle did not complete in time.");
        }
    }

    @Test
    public void testTableViewExists() throws Exception {
        setupStageAndScene();
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            Parent root = scene.getRoot();
            TableView<?> tableView = (TableView<?>) root.lookup(".custom-table");
            assertNotNull(tableView, "TableView with style class 'custom-table' should be present.");
            latch.countDown();
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("testTableViewExists did not complete in time.");
        }
    }

    @Test
    public void testUserInfoLabel() throws Exception {
        setupStageAndScene();
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            Parent root = scene.getRoot();
            // Assumes the user name label has been assigned an fx:id of "userNameLabel"
            Label userNameLabel = (Label) root.lookup("#userNameLabel");
            assertNotNull(userNameLabel, "User info label displaying the user's name should be present.");
            // Assuming Client.user is available and properly initialized
            assertEquals(Client.user.getName(), userNameLabel.getText(), "User info label should display the user's name.");
            latch.countDown();
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("testUserInfoLabel did not complete in time.");
        }
    }

    @Test
    public void testBetListPlaceholder() throws Exception {
        setupStageAndScene();
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            Parent root = scene.getRoot();
            // Assumes the bet list placeholder label has been assigned an fx:id of "betListPlaceholderLabel"
            Label placeholderLabel = (Label) root.lookup("#betListPlaceholderLabel");
            assertNotNull(placeholderLabel, "Placeholder label for bet list should be visible when no bets exist.");
            latch.countDown();
        });
        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("testBetListPlaceholder did not complete in time.");
        }
    }
}