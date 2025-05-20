package edu.sdccd.cisc191.Server;

// This class is a proof of concept class to see that the JPA database is working
// Eventually I will merge the RestController into userDatabase.java
// and merge the Springboot Application(this class) into Server.java
// This is based on the Andrew Huang repo

import edu.sdccd.cisc191.Common.Models.Game;
import edu.sdccd.cisc191.Server.repositories.GameRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Date;


@SpringBootApplication
@EnableJpaRepositories("edu.sdccd.cisc191.Server.repositories")
@EntityScan(basePackages = {"edu.sdccd.cisc191.Common.Models"})
@ComponentScan(basePackages = {"edu.sdccd.cisc191.Server.controllers", "edu.sdccd.cisc191.Server.repositories"})
public class JPARunGameTest {
    private final GameRepository gameRepository;

    public JPARunGameTest(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(JPARunGameTest.class, args);
    }


    public void run(String... args) throws Exception {
        Game game1 = new Game("MMHS", "SRHS", new Date(), "Basketball", 10, 10);
        gameRepository.save(game1);

        Game game2 = new Game("MBHS", "UCHS", new Date(), "Basketball", 10, 10);
        gameRepository.save(game2);
    }
}
