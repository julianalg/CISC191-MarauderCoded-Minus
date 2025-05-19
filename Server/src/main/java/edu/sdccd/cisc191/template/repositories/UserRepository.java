package edu.sdccd.cisc191.template.repositories;

import edu.sdccd.cisc191.template.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
