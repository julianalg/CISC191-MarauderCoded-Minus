package edu.sdccd.cisc191.Server.controllers;

import edu.sdccd.cisc191.Common.Models.Game;
import edu.sdccd.cisc191.Server.API.BaseballGetter;
import edu.sdccd.cisc191.Server.API.BasketballGetter;
import edu.sdccd.cisc191.Server.exceptions.GameNotFoundException;
import edu.sdccd.cisc191.Server.repositories.GameRepository;
import edu.sdccd.cisc191.Server.repositories.GameRepositoryImpl;
import org.joda.time.DateTime;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
class GameController {
    private final GameRepository repository;

    GameController(GameRepositoryImpl repository) {
        this.repository = repository;
        // TODO: Validate repository is not null
    }

    @GetMapping("/games")
    List<Game> all() {
        return repository.findAll();
        // TODO: Add pagination if list gets large
    }

    @GetMapping("/games/byTeam2Odds")
    List<Game> allByTeam2Odds() {
        return repository.findAllSortedByTeam2Odds();
        // TODO: Handle empty or null results
    }

    @PostMapping("/games")
    Game newGame(@RequestBody Game game) {
        // TODO: Validate game before saving
        return repository.save(game);
    }

    @GetMapping("/games/{id}")
    Game one(@PathVariable Long id) {
        // TODO: Validate id
        return repository.findByIdUsingBST(id)
                .orElseThrow(() -> new GameNotFoundException(id));
    }

    @DeleteMapping("/games/{id}")
    void deleteGame(@PathVariable Long id) {
        // TODO: Check existence before deletion
        repository.deleteById(id);
    }

    @GetMapping("/games/odds/{sport}/{id}")
    String getOdds(@PathVariable String sport, @PathVariable Long id) throws ParseException {
        return switch (sport) {
            case "Baseball" -> {
                try {
                    yield new BaseballGetter().getOdd(id);
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                    yield "No bets for this game yet.";
                }
            }
            case "Basketball" -> {
                try {
                    yield new BasketballGetter().getOdd(id);
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                    yield "No bets for this game yet.";
                }
            }
            default -> "Invalid sport";
        };
        // TODO: Improve sport validation and logging
    }

    @GetMapping("/games/sport/{sport}")
    List<Game> getGamesBySport(@PathVariable String sport) {
        // TODO: Normalize sport input
        return repository.findAll().stream()
                .filter(game -> game.getSport().equalsIgnoreCase(sport))
                .collect(Collectors.toList());
    }

    @GetMapping("/games/upcoming")
    List<Game> getUpcomingGames() {
        DateTime now = new DateTime();
        return repository.findAll().stream()
                .filter(game -> game.getGameDate().isAfter(now))
                .sorted(Comparator.comparing(Game::getGameDate))
                .collect(Collectors.toList());
        // TODO: Consider timezone issues
    }

    @GetMapping("/games/dateRange")
    List<Game> findGamesByDateRange(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
        DateTime start = DateTime.parse(startDate);
        DateTime end = DateTime.parse(endDate);
        // TODO: Validate date range

        return repository.findAll().stream()
                .filter(game -> game.getGameDate().isAfter(start) && game.getGameDate().isBefore(end))
                .sorted(Comparator.comparing(Game::getGameDate))
                .collect(Collectors.toList());
    }
}
