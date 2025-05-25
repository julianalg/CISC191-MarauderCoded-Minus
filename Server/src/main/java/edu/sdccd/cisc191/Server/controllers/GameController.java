package edu.sdccd.cisc191.Server.controllers;

import edu.sdccd.cisc191.Common.Models.Game;
import edu.sdccd.cisc191.Server.API.BaseballGetter;
import edu.sdccd.cisc191.Server.API.BasketballGetter;
import edu.sdccd.cisc191.Server.exceptions.GameNotFoundException;
import edu.sdccd.cisc191.Server.repositories.GameRepository;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
class GameController {
    private final GameRepository repository;

    GameController(GameRepository repository) { this.repository = repository; }

    @GetMapping("/games")
    List<Game> all() {
        return repository.findAll();
    }

    @PostMapping("/games")
    Game newGame(@RequestBody Game game) {
        return repository.save(game);
    }

    // Single item

    @GetMapping("/games/{id}")
    Game one(@PathVariable Long id) {

        return repository.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id));
    }

//    @PutMapping("/users/{id}")
//    Game replaceUser(@RequestBody Game newGame, @PathVariable Long id) {
//
//        return repository.findById(id)
//                .map(user -> {
//                    user.(newUser.getName());
//                    return repository.save(user);
//                })
//                .orElseGet(() -> {
//                    return repository.save(newUser);
//                });
//    }

    @DeleteMapping("/games/{id}")
    void deleteUser(@PathVariable Long id) {
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

}
