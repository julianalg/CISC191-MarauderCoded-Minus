package edu.sdccd.cisc191.Server;

import edu.sdccd.cisc191.Common.Models.User;
import edu.sdccd.cisc191.Server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;

@Component
public class UserDatabase extends GenericDatabase<User, Long, UserRepository> {
    
    @Autowired
    public UserDatabase(UserRepository userRepository) throws Exception {
        super(userRepository, User.class);
        loadOrInitializeDatabase();
    }

    @Override
    protected void initializeDefaultEntities() {
        List<User> defaultUsers = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            String userId = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
            defaultUsers.add(new User(userId, (int) (Math.random() * 1000)));
        }
        repository.saveAll(defaultUsers);
    }

    @Override
    protected String getFileName() {
        return "users.json";
    }

    @Override
    protected String getEntityName() {
        return "User";
    }

    public User findByName(String name) {
        return repository.findByName(name);
    }
}