package edu.sdccd.cisc191.Server.repositories;

import edu.sdccd.cisc191.Common.Models.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
}
