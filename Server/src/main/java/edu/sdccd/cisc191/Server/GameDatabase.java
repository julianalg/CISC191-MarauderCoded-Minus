package edu.sdccd.cisc191.Server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import edu.sdccd.cisc191.Common.Models.Game;
import edu.sdccd.cisc191.Common.GameBST;
import edu.sdccd.cisc191.Common.Models.User;
import edu.sdccd.cisc191.Server.API.BaseballGetter;
import edu.sdccd.cisc191.Server.API.BasketballGetter;
import edu.sdccd.cisc191.Server.repositories.GameRepository;
import jakarta.annotation.PreDestroy;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.io.FileWriter;
import java.lang.reflect.Array;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A singleton class that manages a database of games.
 * It provides functionalities to load, save, and access the database.
 *
 * The data is stored in JSON format, and the database is thread-safe
 * to ensure proper operation in concurrent environments.
 *
 * @author Andy Ly
 */
public class GameDatabase {

    // Singleton instance
    private final GameRepository gameRepository;

    @Value("Server/src/main/resources/games.json")
    private String resourcePath;

    //Tree sorts by game id and odds
    private GameBST.BinarySearchTree<Game> treeById;
    private GameBST.BinarySearchTree<Game> treeByTeam1Odds;
    private GameBST.BinarySearchTree<Game> treeByTeam2Odds;


    private File getOrCreateDatabaseFile() { // Remove static modifier
        // First, try to get the file from resources
        URL filePath = GameDatabase.class.getResource("/games.json");
        if (filePath != null) {
            try {
                return new File(filePath.toURI());
            } catch (Exception e) {
                // Fall through to use the configured path
            }
        }
        // If resource not found, create the file in the specified directory
        File file = new File(resourcePath);
        try {
            File parentDir = file.getParentFile();
            if (parentDir != null) {
                parentDir.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            return file;
        } catch (IOException e) {
            throw new RuntimeException("Failed to create database file at " + resourcePath, e);
        }
    }

    public GameDatabase(GameRepository gameRepository) {

        this.gameRepository = gameRepository;

    }

    /**
     * Reconstructs the BSTs from the current list of games

    private void rebuildTrees() {
        treeById = GameBST.buildGameIdTree(gameDatabase);
        treeByTeam1Odds = GameBST.buildOddsTree(gameDatabase);
        treeByTeam2Odds = GameBST.buildOddsTree(gameDatabase);
    }

    /**
     * Initializes the game database with default data.
     */


    public void loadOrInitializeDatabase() throws Exception {


        if (gameRepository.count() == 0) {
            File file = getOrCreateDatabaseFile();
            if (file.exists()) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    CollectionType listType = objectMapper.getTypeFactory()
                            .constructCollectionType(List.class, User.class);
                    List<Game> games = objectMapper.readValue(file, listType);

                    for (Game game : games) {
                        Instant inputInstant = Instant.parse(game.getGameDate().toString());
                        Instant now = Instant.now();
                        if (inputInstant.isBefore(now)) {
                            games.remove(game); // Remove game from database if in the past
                        } else {
                            System.out.println("The date is in the future.");
                        }
                    }
                    gameRepository.saveAll(games);
                    System.out.println("GameDatabase loaded from file.");
                } catch (Exception e) {
                    System.out.println("EXCEPTION CAUGHT");
                    System.out.println("Failed to load GameDatabase from file. Initializing with default data.");
                    initializeDefaultGames();
                }
            } else {
                System.out.println("GameDatabase file not found. Initializing with default data.");
                initializeDefaultGames();
            }
        }


        saveToFile();
    }

    private void initializeDefaultGames() {
        return;
    }

    public void saveToFile() {
        System.out.println("Save to file method triggered");
        try (Writer writer = new FileWriter(getOrCreateDatabaseFile())) {
            ObjectMapper objectMapper = new ObjectMapper();

            List<Game> games = gameRepository.findAll();
            System.out.println("Total users in database: " + games.size());
            objectMapper.writeValue(writer, games);
            System.out.println("GameDatabase saved to file: " + getOrCreateDatabaseFile().getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Updates the game database from the API
     */
    public void updateDatabaseFromAPI() throws Exception {
        BaseballGetter baseballGetter = new BaseballGetter();
        ArrayList<Game> baseballGames = baseballGetter.getGames("Baseball");

        for (Game game : baseballGames) {
//            System.out.println("Adding game " + game.getId() + " to database");
            gameRepository.save(game);
        }

        BasketballGetter basketballGetter = new BasketballGetter();
        ArrayList<Game> basketballGames = basketballGetter.getGames("Basketball");

        for (Game game : basketballGames) {
//            System.out.println("Adding game " + game.getId() + " to database");
            gameRepository.save(game);
        }

//        System.out.println("Total games in database: " + baseballGames.size());

    }


    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    public Game saveUser(Game game) {
        return gameRepository.save(game);
    }

    public void deleteUser(Game game) {
        gameRepository.delete(game);
    }

    public Game findUserById(Long id) {
        return gameRepository.findById(id).orElse(null);
    }

    public long getSize() {
        return gameRepository.count();
    }

}
