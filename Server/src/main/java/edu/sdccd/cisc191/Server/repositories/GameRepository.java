package edu.sdccd.cisc191.Server.repositories;

import edu.sdccd.cisc191.Common.Models.Game;
import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findAllSortedByTeam1Odds();
    List<Game> findAllSortedByTeam2Odds();
    List<Game> findAllSortedByDate();
    Optional<Game> findByIdUsingBST(Long id);
    List<Game> findGamesByDateRange(DateTime start, DateTime end);
}