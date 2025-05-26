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
    }

    // Changed to use BST-sorted by team1 odds
    @GetMapping("/games")
    List<Game> all() {
        return repository.findAll();
    }

    // Added new endpoint for team2 odds sorting
    @GetMapping("/games/byTeam2Odds")
    List<Game> allByTeam2Odds() {
        return repository.findAllSortedByTeam2Odds();
    }

    @PostMapping("/games")
    Game newGame(@RequestBody Game game) {
        return repository.save(game);
    }

    // Updated to use BST-based search
    @GetMapping("/games/{id}")
    Game one(@PathVariable Long id) {
        return repository.findByIdUsingBST(id)
                .orElseThrow(() -> new GameNotFoundException(id));
    }

    @DeleteMapping("/games/{id}")
    void deleteGame(@PathVariable Long id) {
        repository.deleteById(id);
    }

    @GetMapping("/games/odds/{sport}/{id}")
    String getOdds(@PathVariable String sport, @PathVariable Long id) throws ParseException {
        return switch (sport) {
            case "Baseball" -> {
                try {
                    BaseballGetter baseballGetter = new BaseballGetter();
                    yield baseballGetter.getOdd(id);
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                    yield "No bets for this game yet.";
                }
            }
            case "Basketball" -> {
                try {
                    BasketballGetter basketballGetter = new BasketballGetter();
                    yield basketballGetter.getOdd(id);
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                    yield "No bets for this game yet.";
                }
            }
            default -> "Invalid sport";
        };
    }

    // Get games by sport
    @GetMapping("/games/sport/{sport}")
    List<Game> getGamesBySport(@PathVariable String sport) {
        return repository.findAll().stream()
                .filter(game -> game.getSport().equalsIgnoreCase(sport))
                .collect(Collectors.toList());
    }

    // Get future games
    @GetMapping("/games/upcoming")
    List<Game> getUpcomingGames() {
        DateTime now = new DateTime();
        return repository.findAll().stream()
                .filter(game -> game.getGameDate().isAfter(now))
                .sorted(Comparator.comparing(Game::getGameDate))
                .collect(Collectors.toList());
    }

    @GetMapping("/games/dateRange")
    List<Game> findGamesByDateRange(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {
        DateTime start = DateTime.parse(startDate);
        DateTime end = DateTime.parse(endDate);

        return repository.findAll().stream()
                .filter(game -> game.getGameDate().isAfter(start)
                        && game.getGameDate().isBefore(end))
                .sorted(Comparator.comparing(Game::getGameDate))
                .collect(Collectors.toList());
    }

}