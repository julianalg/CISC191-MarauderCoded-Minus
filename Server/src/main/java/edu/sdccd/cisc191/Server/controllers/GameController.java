package edu.sdccd.cisc191.Server.controllers;

import edu.sdccd.cisc191.Common.Models.Game;
import edu.sdccd.cisc191.Server.exceptions.UserNotFoundException;
import edu.sdccd.cisc191.Server.repositories.GameRepository;
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
                .orElseThrow(() -> new UserNotFoundException(id));
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

}
