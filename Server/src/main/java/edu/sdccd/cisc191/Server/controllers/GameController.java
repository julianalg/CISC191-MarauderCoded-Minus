package edu.sdccd.cisc191.Server.controllers;

import edu.sdccd.cisc191.Common.Models.Game;
import edu.sdccd.cisc191.Server.API.BaseballGetter;
import edu.sdccd.cisc191.Server.API.BasketballGetter;
import edu.sdccd.cisc191.Server.exceptions.GameNotFoundException;
import edu.sdccd.cisc191.Server.repositories.GameRepository;
import edu.sdccd.cisc191.Server.repositories.GameRepositoryImpl;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    // Existing odds endpoint remains unchanged
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
}