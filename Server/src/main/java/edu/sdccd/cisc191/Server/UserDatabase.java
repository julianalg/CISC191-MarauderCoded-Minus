package edu.sdccd.cisc191.Server;

import edu.sdccd.cisc191.Common.Models.User;
import edu.sdccd.cisc191.Server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A Spring-managed component responsible for managing the User entity database.
 * <p>
 * This class extends GenericDatabase to provide file-based persistence,
 * default data initialization, and a custom query for looking up users by name.
 * </p>
 */
@Component
public class UserDatabase extends GenericDatabase<User, Long, UserRepository> {

    /**
     * Constructs a new {@code UserDatabase} and loads or initializes the user data.
     *
     * @param userRepository the repository for User entities
     * @throws Exception if loading or initializing the database fails
     */
    @Autowired
    public UserDatabase(UserRepository userRepository) throws Exception {
        super(userRepository, User.class);
        loadOrInitializeDatabase();
    }

    /**
     * Initializes the user database with default users.
     * <p>
     * A primary user named "Chase" with a starting balance of 1000 is added,
     * along with four additional users with randomly generated names and balances.
     * </p>
     */
    @Override
    protected void initializeDefaultEntities() {
        // Push user with ID 1 to be the primary user
        User primaryUser = new User("Chase", 1000);
        repository.save(primaryUser);

        // Generate random other users
        List<User> defaultUsers = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            String userId = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
            defaultUsers.add(new User(userId, (int) (Math.random() * 1000)));
        }
        repository.saveAll(defaultUsers);
    }

    /**
     * Returns the file name used for saving/loading user data.
     *
     * @return the file name "users.json"
     */
    @Override
    protected String getFileName() {
        return "users.json";
    }

    /**
     * Returns the string identifier for the type of entity managed.
     *
     * @return the string "User"
     */
    @Override
    protected String getEntityName() {
        return "User";
    }

    /**
     * Finds a user by their name using the repository's query method.
     *
     * @param name the name of the user
     * @return the  User with the specified name, or  null if not found
     */
    public User findByName(String name) {
        return repository.findByName(name);
    }
}
