package edu.sdccd.cisc191.template;

// This class is a proof of concept class to see that the JPA database is working
// Eventually I will merge the RestController into userDatabase.java
// and merge the Springboot Application(this class) into Server.java
// This is based on the Andrew Huang repo

import edu.sdccd.cisc191.template.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
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
        User alice = new User();
        alice.setName("Alice");
        userRepository.save(alice);

        User bob = new User();
        bob.setName("Bob");
        userRepository.save(bob);
    }
}
