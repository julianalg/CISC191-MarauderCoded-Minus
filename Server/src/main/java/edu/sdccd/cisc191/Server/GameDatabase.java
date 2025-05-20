package edu.sdccd.cisc191.Server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import edu.sdccd.cisc191.Common.Models.Game;
import edu.sdccd.cisc191.Common.GameBST;
import edu.sdccd.cisc191.Common.Models.User;
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
import java.net.URL;
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
@SpringBootApplication
@EnableJpaRepositories("edu.sdccd.cisc191.Server.repositories")
@EntityScan(basePackages = {"edu.sdccd.cisc191.Common.Models"})
@ComponentScan(basePackages = {"edu.sdccd.cisc191.Server.controllers", "edu.sdccd.cisc191.Server.repositories"})
public class GameDatabase implements CommandLineRunner {

    // Singleton instance
    private static GameDatabase instance;
    private final GameRepository gameRepository;

    @Value("Server/src/main/resources/games.json")
    private String resourcePath;

    //Tree sorts by game id and odds
    private GameBST.BinarySearchTree<Game> treeById;
    private GameBST.BinarySearchTree<Game> treeByTeam1Odds;
    private GameBST.BinarySearchTree<Game> treeByTeam2Odds;


    private File getOrCreateDatabaseFile() { // Remove static modifier
        // First, try to get the file from resources
        URL filePath = UserDatabase.class.getResource("/games.json");
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

    @Autowired
    public GameDatabase(GameRepository gameRepository) {

        this.gameRepository = gameRepository;
        instance = this;

    }

    public static synchronized GameDatabase getInstance() {
        if (instance == null) {
            throw new IllegalStateException("GameDatabase instance has not been initialized yet.");
        }
        return instance;
    }

    public static void main(String[] args) { SpringApplication.run(GameDatabase.class, args);}


    @Override
    public void run(String... args) throws Exception {

        loadOrInitializeDatabase();

    }

    void loadOrInitializeDatabase() {
        if (gameRepository.count() == 0) {
            File file = getOrCreateDatabaseFile();
            if (file.exists()) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    CollectionType listType = objectMapper.getTypeFactory()
                            .constructCollectionType(List.class, Game.class);
                    List<Game> users = objectMapper.readValue(file, listType);
                    gameRepository.saveAll(users);
                    System.out.println("UserDatabase loaded from file.");
                } catch (Exception e) {
                    System.out.println("EXCEPTION CAUGHT");
                    System.out.println("Failed to load UserDatabase from file. Initializing with default data.");
                    initializeDefaultGames();
                }
            } else {
                System.out.println("UserDatabase file not found. Initializing with default data.");
                initializeDefaultGames();
            }
        }
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

    void loadOrInitializeDatabase() {
        if (gameRepository.count() == 0) {
            File file = getOrCreateDatabaseFile();
            if (file.exists()) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    CollectionType listType = objectMapper.getTypeFactory()
                            .constructCollectionType(List.class, User.class);
                    List<Game> games = objectMapper.readValue(file, listType);
                    gameRepository.saveAll(games);
                    System.out.println("UserDatabase loaded from file.");
                } catch (Exception e) {
                    System.out.println("EXCEPTION CAUGHT");
                    System.out.println("Failed to load UserDatabase from file. Initializing with default data.");
                    initializeDefaultGames();
                }
            } else {
                System.out.println("UserDatabase file not found. Initializing with default data.");
                initializeDefaultGames();
            }
        }
    }

    private void initializeDefaultGames() {
        return;
    }

    @PreDestroy
    public void saveToFile() {
        System.out.println("Save to file method triggered");
        try (Writer writer = new FileWriter(getOrCreateDatabaseFile())) {
            ObjectMapper objectMapper = new ObjectMapper();

            List<Game> users = gameRepository.findAll();
            System.out.println("Total users in database: " + users.size());
            users.forEach(user -> System.out.println("User ID: " + user.getId() + ", Name: " + user.getName()));

            objectMapper.writeValue(writer, users);
            System.out.println("UserDatabase saved to file: " + getOrCreateDatabaseFile().getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Updates the game database from the API
     */
    void updateDatabaseFromAPI() throws ParseException {

    }

    public List<User> getAllUsers() {
        return gameRepository.findAll();
    }

    public User saveUser(User user) {
        return gameRepository.save(user);
    }

    public void deleteUser(User user) {
        gameRepository.delete(user);
    }

    public User findUserById(Long id) {
        return gameRepository.findById(id).orElse(null);
    }

    public User findUserByName(String name) {
        return gameRepository.findByName(name);
    }

    public long getSize() {
        return gameRepository.count();
    }

}
