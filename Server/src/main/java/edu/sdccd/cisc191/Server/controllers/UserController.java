package edu.sdccd.cisc191.Server.controllers;

import java.util.List;

import edu.sdccd.cisc191.Common.Models.Bet;
import edu.sdccd.cisc191.Common.Models.Game;
import edu.sdccd.cisc191.Common.Models.User;
import edu.sdccd.cisc191.Server.exceptions.UserNotFoundException;
import edu.sdccd.cisc191.Server.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.*;

// Pulled straight from andrew huang repo

@RestController
class UserController {

    private final UserRepository repository;

    UserController(UserRepository repository) {
        this.repository = repository;
    }


    // Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("/users")
    List<User> all() {
        return repository.findAll();
    }
    // end::get-aggregate-root[]

    @PostMapping("/users")
    User newUser(@RequestBody User newUser) {
        return repository.save(newUser);
    }

    // Single item

    @GetMapping("/users/{id}")
    User one(@PathVariable Long id) {

        return repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @PutMapping("/users/{id}")
    User replaceUser(@RequestBody User newUser, @PathVariable Long id) {
        return repository.findById(id)
                .map(user -> {
                    user.setName(newUser.getName());
                    user.setMoney((int) newUser.getMoney());
                    return repository.save(user);
                })
                .orElseGet(() -> {
                    newUser.setId(id);
                    return repository.save(newUser);
                });
    }

    @DeleteMapping("/users/{id}")
    void deleteUser(@PathVariable Long id) {
        repository.deleteById(id);
    }

    @PatchMapping("/{id}/bets")
    @Transactional
    public User addBet(
            @PathVariable Long id,
            @RequestBody IncomingBetDTO dto      // ← see below
    ) {
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        // 1. fetch the managed Game
        Game game = repository.findById(dto.getGameId())
                .orElseThrow(() -> new GameNotFoundException(dto.getGameId()));

        // 2. build a new Bet instance
        Bet bet = new Bet(game, dto.getBetAmt(), dto.getBetTeam());

        // 3. add & save
        user.addBet(bet);
        return repository.save(user);
    }

}