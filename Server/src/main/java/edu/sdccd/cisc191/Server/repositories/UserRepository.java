package edu.sdccd.cisc191.Server.repositories;

import edu.sdccd.cisc191.Common.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
