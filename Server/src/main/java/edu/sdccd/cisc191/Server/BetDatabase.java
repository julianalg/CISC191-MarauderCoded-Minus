package edu.sdccd.cisc191.server;

import edu.sdccd.cisc191.Common.Models.Game;
import edu.sdccd.cisc191.Common.Models.User;
import edu.sdccd.cisc191.Server.GameDatabase;
import edu.sdccd.cisc191.Server.UserDatabase;
import edu.sdccd.cisc191.Server.repositories.GameRepository;
import edu.sdccd.cisc191.Server.repositories.UserRepository;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.FluentQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@SpringBootApplication
@EnableJpaRepositories("edu.sdccd.cisc191.Server.repositories")
@EntityScan(basePackages = {"edu.sdccd.cisc191.Common.Models"})
@ComponentScan(basePackages = {"edu.sdccd.cisc191.Server.controllers", "edu.sdccd.cisc191.Server.repositories"})
public class BetDatabase implements CommandLineRunner {
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final GameDatabase gameDatabase;
    private final UserDatabase userDatabase;

    public BetDatabase(UserRepository userRepository, GameRepository gameRepository) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.gameDatabase = new GameDatabase(gameRepository);
        this.userDatabase = new UserDatabase(userRepository);
    }

    public static void main(String[] args) {
        SpringApplication.run(BetDatabase.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        System.out.println("App is running...");

        gameDatabase.updateDatabaseFromAPI();
    }

    @PreDestroy
    public void saveAllToFiles(){

        gameDatabase.saveToFile();
        userDatabase.saveToFile();

    }

    public List<Game> getGames() {
        return gameDatabase.getAllGames();
    }

    public GameDatabase getGameDBInstance() {
        return gameDatabase;
    }

    public UserDatabase getUserDBInstance() {
        return userDatabase;
    }
}