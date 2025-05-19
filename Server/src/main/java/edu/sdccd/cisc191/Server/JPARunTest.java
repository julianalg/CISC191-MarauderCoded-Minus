package edu.sdccd.cisc191.Server;

// This class is a proof of concept class to see that the JPA database is working
// Eventually I will merge the RestController into userDatabase.java
// and merge the Springboot Application(this class) into Server.java
// This is based on the Andrew Huang repo

import edu.sdccd.cisc191.Common.Models.User;
import edu.sdccd.cisc191.Server.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.annotation.ComponentScan;



@SpringBootApplication
@EnableJpaRepositories("edu.sdccd.cisc191.Server.repositories")
@EntityScan(basePackages = {"edu.sdccd.cisc191.Common.Models"})
@ComponentScan(basePackages = {"edu.sdccd.cisc191.Server.controllers", "edu.sdccd.cisc191.Server.repositories"})
public class JPARunTest implements CommandLineRunner {
    private final UserRepository userRepository;

    public JPARunTest(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(JPARunTest.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        User alice = new User("Alice", 1000);
        userRepository.save(alice);

        User bob = new User("Bob", 2000);
        userRepository.save(bob);
    }
}
