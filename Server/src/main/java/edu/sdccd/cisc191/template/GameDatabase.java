package edu.sdccd.cisc191.template;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.io.FileWriter;
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
    private static final String FILE_PATH = "Server/Resources/games.json";

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
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                // Register subtypes explicitly if necessary (optional if using annotations)
                objectMapper.registerSubtypes(Football.class, Basketball.class, Tennis.class);

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
            gameDatabase.add(new Basketball(
                    String.format("Team %d", count),
                    String.format("Team %d", count + 1), new Date(),
                    randomDate));
            count += 2;
        }
    }

    /**
     * Saves the current state of the game database to a JSON file.
     */
    void saveToFile() {
        try (Writer writer = new FileWriter(FILE_PATH)) {
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
     * @return The size of the database as a  String .
     */
    public synchronized String getSize() {
        return String.valueOf(gameDatabase.size());
    }
}
