package edu.sdccd.cisc191.Server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import edu.sdccd.cisc191.Common.Models.User;
import edu.sdccd.cisc191.Server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


// This class was meant to be a singleton, not sure if its still true after making it
// a springboot database
public class UserDatabase {

    private final UserRepository userRepository;
    
    @Value("${app.database.file-path}")
    private String resourcePath; // Changed from static to instance field
    
    private File getOrCreateDatabaseFile() { // Remove static modifier
        // First, try to get the file from resources
        URL filePath = UserDatabase.class.getResource("/users.json");
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

    public UserDatabase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void loadOrInitializeDatabase() {
        if (userRepository.count() == 0) {
            File file = getOrCreateDatabaseFile();
            if (file.exists()) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    CollectionType listType = objectMapper.getTypeFactory()
                            .constructCollectionType(List.class, User.class);
                    List<User> users = objectMapper.readValue(file, listType);
                    System.out.println("Total users in file: " + users.size());
                    for (User user : users) {
                        System.out.println("User ID: " + user.getId() + ", Name: " + user.getName());
                    }
                    userRepository.saveAll(users);
                    System.out.println("UserDatabase loaded from file.");
                } catch (Exception e) {
                    System.out.println("EXCEPTION CAUGHT");
                    System.out.println("Failed to load UserDatabase from file. Initializing with default data.");
                    initializeDefaultUsers();
                }
            } else {
                System.out.println("UserDatabase file not found. Initializing with default data.");
                initializeDefaultUsers();
            }
        }
    }

    private void initializeDefaultUsers() {
        List<User> defaultUsers = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            String userId = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
            defaultUsers.add(new User(userId, (int) (Math.random() * 1000)));
            System.out.println("Creating user " + (i+1) + userId);
        }
        List<User> savedUsers = userRepository.saveAll(defaultUsers);
        System.out.println("Total users saved: " + savedUsers.size());

        List<User> users = userRepository.findAll();
        System.out.println("Total users in database: " + users.size());

    }

    public void saveToFile() {
        System.out.println("Save to file method triggered");
        try (Writer writer = new FileWriter(getOrCreateDatabaseFile())) {
            ObjectMapper objectMapper = new ObjectMapper();

            List<User> users = userRepository.findAll();
            System.out.println("Total users in database: " + users.size());
            users.forEach(user -> System.out.println("User ID: " + user.getId() + ", Name: " + user.getName()));

            objectMapper.writeValue(writer, users);
            System.out.println("UserDatabase saved to file: " + getOrCreateDatabaseFile().getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public User findUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User findUserByName(String name) {
        return userRepository.findByName(name);
    }

    public long getSize() {
        return userRepository.count();
    }

}