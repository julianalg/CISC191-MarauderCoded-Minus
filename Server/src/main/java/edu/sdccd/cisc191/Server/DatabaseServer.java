package edu.sdccd.cisc191.Server;

import edu.sdccd.cisc191.Server.repositories.GameRepository;
import edu.sdccd.cisc191.Server.repositories.UserRepository;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.IOException;

// A class to instantiate the databases and run a server on the port specified
// in the application.yml file. To modify how the server handles requests,
// modify the classes within the controller folder
// To add new types of queries(findByName, findByBet, findByMoney, etc),
// modify the classes in the repository folder
@SpringBootApplication
@EnableJpaRepositories("edu.sdccd.cisc191.Server.repositories")
@EntityScan(basePackages = {"edu.sdccd.cisc191.Common.Models"})
@ComponentScan(basePackages = {
        "edu.sdccd.cisc191.Server.controllers",
        "edu.sdccd.cisc191.Server.repositories",
        "edu.sdccd.cisc191.Server"
        })

public class DatabaseServer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final GameDatabase gameDatabase;
    private final UserDatabase userDatabase;

    @Autowired
    public DatabaseServer(UserRepository userRepository, GameRepository gameRepository, GameDatabase gameDatabase, UserDatabase userDatabase) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.gameDatabase = gameDatabase;
        this.userDatabase = userDatabase;
    }

    public static void main(String[] args) {
        SpringApplication.run(DatabaseServer.class, args);
    }


    @Override
    public void run(String... args) {
        System.out.println("App is running...");
    }

    @PreDestroy
    public void saveAllToFiles() {

        gameDatabase.saveToFile();
        userDatabase.saveToFile();

    }
}