package edu.sdccd.cisc191.template;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * A singleton class that manages a database of users. It provides
 * functionalities for loading, saving, and accessing user data stored
 * in JSON format. This class ensures thread-safe operations in
 * concurrent environments.
 *
 * The user data is stored in a synchronized list and can be
 * initialized with default values or loaded from an existing JSON file.
 *
 * @author Andy Ly
 */
public class UserDatabase {

    // Singleton instance
    private static UserDatabase instance;

    // Synchronized list to store user data
    private static final List<User> userDatabase = Collections.synchronizedList(new ArrayList<>());

    // File path for storing user data
    private static final URL FILE_PATH = UserDatabase.class.getResource("Users.json");

    /**
     * Private constructor to prevent instantiation outside the class.
     * Loads the user data from a JSON file or initializes it with
     * default values if the file doesn't exist.
     */
    private UserDatabase() {
        loadOrInitializeDatabase();
    }

    /**
     * Retrieves the singleton instance of the UserDatabase class.
     *
     * @return The singleton UserDatabase instance.
     */
    public static synchronized UserDatabase getInstance() {
        if (instance == null) {
            instance = new UserDatabase();
        }
        return instance;
    }

    /**
     * Loads the user database from a JSON file if it exists or initializes
     * it with default data if the file is not found.
     */
    void loadOrInitializeDatabase() {
        File file = new File(FILE_PATH.getFile());
        if (file.exists()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                CollectionType listType = objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, User.class);
                List<User> users = objectMapper.readValue(file, listType);
                userDatabase.clear();
                userDatabase.addAll(users);
                System.out.println("UserDatabase loaded from file.");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to load UserDatabase from file. Initializing with default data.");
                initializeDefaultUsers();
                saveToFile();
            }
        } else {
            System.out.println("UserDatabase file not found. Initializing with default data.");
            initializeDefaultUsers();
            saveToFile();
        }
    }

    /**
     * Initializes the user database with default data, creating
     * a list of users with randomized IDs and balances.
     */
    private void initializeDefaultUsers() {
        for (int i = 0; i < 5; i++) {
            userDatabase.add(new User(
                    UUID.randomUUID().toString().replace("-", "").substring(0, 10),
                    (int) (Math.random() * 1000)));
        }
    }

    /**
     * Saves the current state of the user database to a JSON file.
     */
    public void saveToFile() {
        try (Writer writer = new FileWriter(FILE_PATH.getFile())) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(writer, userDatabase);
            System.out.println("UserDatabase saved to file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves an unmodifiable view of the user database.
     *
     * @return An unmodifiable  List  of users.
     */
    public synchronized List<User> getUserDatabase() {
        return Collections.unmodifiableList(userDatabase);
    }

    /**
     * Retrieves the size of the user database.
     *
     * @return The size of the database as a String .
     */
    public synchronized String getSize() {
        return String.valueOf(userDatabase.size());
    }
}
