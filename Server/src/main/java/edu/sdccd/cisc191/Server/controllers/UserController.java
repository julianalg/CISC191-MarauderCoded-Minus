package edu.sdccd.cisc191.Server.controllers;

import java.util.*;

import edu.sdccd.cisc191.Common.IncomingBetDTO;
import edu.sdccd.cisc191.Common.Models.Bet;
import edu.sdccd.cisc191.Common.Models.Game;
import edu.sdccd.cisc191.Common.Models.User;
import edu.sdccd.cisc191.Server.API.BaseballGetter;
import edu.sdccd.cisc191.Server.exceptions.GameNotFoundException;
import edu.sdccd.cisc191.Server.exceptions.UserNotFoundException;
import edu.sdccd.cisc191.Server.repositories.GameRepository;
import edu.sdccd.cisc191.Server.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.joda.time.DateTime;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.*;

// Pulled straight from andrew huang repo

@RestController
class UserController {

    private final UserRepository repository;
    private final GameRepository gRepository;

    UserController(UserRepository repository, GameRepository gameRepository) {
        this.repository = repository;
        this.gRepository = gameRepository;
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
        Game game = gRepository.findById(dto.getGameId())
                .orElseThrow(() -> new GameNotFoundException(dto.getGameId()));

        // 2. build a new Bet instance
        Bet bet = new Bet(game, dto.getBetAmt(), dto.getBetTeam());

        // 3. add & save
        user.addBet(bet);
        return repository.save(user);
    }

    @GetMapping("/updateAllBets")
    @Transactional
    public String updateAllBets() throws ParseException {
        User[] users = repository.findAll().toArray(new User[0]);
        for (User user : users) {
            int dbId = Math.toIntExact(user.getId());
            List<Bet> toRemove = new ArrayList<>();
            List<Bet> betList = user.getBets();
            System.out.println("Processing user: " + user.getName() + " with " + betList.size() + " bets. cowabunga");
            System.out.println("Bets" + betList + "cowabunga");
            Iterator<Bet> it = betList.iterator();
            while (it.hasNext()) {
                Bet bet = it.next();
                Game betGame = bet.getGame();
                DateTime gameDate = betGame.getGameDate();
                if (gameDate.isBefore(new DateTime(new Date()))) {
                    if (Objects.equals(betGame.getSport(), "Baseball")) {
                        BaseballGetter baseballGetter = new BaseballGetter();
                        String winner = baseballGetter.getWinner(betGame.getId());
                        if (winner.equals(bet.getBetTeam())) {
                            user.setMoney(user.getMoney() + bet.getWinAmt());
                        } else {
                            user.setMoney(user.getMoney() - bet.getBetAmt());
                        }

                        toRemove.add(bet);

                    }
                }
            }

            // one single mutation & save
            user.getBets().removeAll(toRemove);
            replaceUser(user, (long) dbId);
        }
        return "Bad things have happened here";
    }
}