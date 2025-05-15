package edu.sdccd.cisc191.template;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.json.simple.parser.ParseException;

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
public class GameDatabase {

    // Singleton instance
    private static GameDatabase instance;

    private static final List<Game> gameDatabase = Collections.synchronizedList(new ArrayList<>());

    // File path for storing game data
    private static final URL FILE_PATH = GameDatabase.class.getResource("games.json");

    //Tree sorts by game id and odds
    private GameBST.BinarySearchTree<Game> treeById;
    private GameBST.BinarySearchTree<Game> treeByTeam1Odds;
    private GameBST.BinarySearchTree<Game> treeByTeam2Odds;

    /**
     * Private constructor to prevent instantiation outside the class.
     * Initializes the database by either loading data from the file
     * or creating a default dataset.
     */
    private GameDatabase() {
        loadOrInitializeDatabase();
    }

    /**
     * Retrieves the singleton instance of the GameDatabase class.
     *
     * @return The singleton GameDatabase instance.
     */
    public static synchronized GameDatabase getInstance() {
        if (instance == null) {
            instance = new GameDatabase();
        }
        return instance;
    }

    /**
     * Loads the game database from a JSON file if it exists, or initializes
     * it with default data if the file is not found.
     */
    void loadOrInitializeDatabase() {
        File file = new File(FILE_PATH.getFile());
        if (file.exists()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                // Register subtypes explicitly if necessary (optional if using annotations)
                CollectionType listType = objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, Game.class);
                List<Game> games = objectMapper.readValue(file, listType);

                gameDatabase.clear();
                gameDatabase.addAll(games);
                System.out.println("GameDatabase loaded from file.");
                //this.updateDatabaseFromAPI();
                //System.out.println("GameDatabase updated from API.");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to load GameDatabase from file. Initializing with default data.");
                initializeDefaultGames();
                saveToFile();
            } //catch (ParseException e) {
                //throw new RuntimeException(e);
            //}
        } else {
            System.out.println("GameDatabase file not found. Initializing with default data.");
            initializeDefaultGames();
            saveToFile();
        }
        rebuildTrees();
    }

    /**
     * Reconstructs the BSTs from the current list of games
     */
    private void rebuildTrees() {
        treeById = GameBST.buildGameIdTree(gameDatabase);
        treeByTeam1Odds = GameBST.buildOddsTree(gameDatabase);
        treeByTeam2Odds = GameBST.buildOddsTree(gameDatabase);
    }

    /**
     * Initializes the game database with default data.
     */
    private void initializeDefaultGames() {
        // To generate a date between now and 2 years from now
        Date d1 = new Date();
        Date d2 = new Date(2025, 1, 1);
        // To generate default team numbers
        int count = 0;
        for (int i = 0; i < 6; i++) {
            Date randomDate = new Date(ThreadLocalRandom.current()
                    .nextLong(d1.getTime(), d2.getTime()));
            gameDatabase.add(new Game(
                    String.format("Team %d", count),
                    String.format("Team %d", count + 1), new Date(), "Basketball", 0, 0));
            count += 2;
        }
    }

    /**
     * Saves the current state of the game database to a JSON file.
     */
    void saveToFile() {
        try (Writer writer = new FileWriter(FILE_PATH.getFile())) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(writer, gameDatabase);
            System.out.println("GameDatabase saved to file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the game database from the API
     */
    void updateDatabaseFromAPI() throws ParseException {

    }

    /**
     * Retrieves an unmodifiable view of the game database.
     *
     * @return An unmodifiable List of games.
     */
    public synchronized List<Game> getGameDatabase() {
        return Collections.unmodifiableList(gameDatabase);
    }

    /**
     * Gets the size of the game database.
     *
     * @return The size of the database as an Int .
     */
    public synchronized int getSize() {
        return gameDatabase.size();
    }

    /**
     * Returns the BSTs sorted by game id and odds
     */
    public GameBST.BinarySearchTree<Game> getTreeById() {
        return treeById;
    }

    public GameBST.BinarySearchTree<Game> getTreeByTeam1Odds() {
        return treeByTeam1Odds;
    }

    public GameBST.BinarySearchTree<Game> getTreeByTeam2Odds() {
        return treeByTeam2Odds;
    }
}
