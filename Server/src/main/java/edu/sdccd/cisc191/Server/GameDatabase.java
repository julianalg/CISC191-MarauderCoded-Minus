package edu.sdccd.cisc191.Server;

import edu.sdccd.cisc191.Common.Models.Game;
import edu.sdccd.cisc191.Server.API.BaseballGetter;
import edu.sdccd.cisc191.Server.API.BasketballGetter;
import edu.sdccd.cisc191.Server.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;

@Component
public class GameDatabase extends GenericDatabase<Game, Long, GameRepository> {
    
    @Autowired
    public GameDatabase(GameRepository gameRepository) throws Exception {
        super(gameRepository, Game.class);
        loadOrInitializeDatabase();
        try {
            updateDatabaseFromAPI();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Game Database loaded.");
        System.out.println("Game Database contents:");
        for (Game game : repository.findAll()) {
            System.out.println(game);
        }
    }
    
    @Override
    protected void initializeDefaultEntities() throws Exception {
        // Implementation for default games if needed
        List<Game> defaultGames = new ArrayList<>();
        repository.saveAll(defaultGames);
    }

    @Override
    protected String getFileName() {
        return "games.json";
    }

    @Override
    protected String getEntityName() {
        return "Game";
    }

    public void updateDatabaseFromAPI() throws Exception {
        BaseballGetter baseballGetter = new BaseballGetter();
        ArrayList<Game> baseballGames = baseballGetter.getGames("Baseball");

        System.out.println("Found " + baseballGames.size() + " new baseball games." );
        for (Game game : baseballGames) {
            try {
                repository.save(game);
            } catch (DataIntegrityViolationException e) {
                System.out.println("Game " + game.getId() + " already exists in database.");
            }
        }

        BasketballGetter basketballGetter = new BasketballGetter();
        ArrayList<Game> basketballGames = basketballGetter.getGames("Basketball");

        System.out.println("Found " + basketballGames.size() + " new basketball games." );
        for (Game game : basketballGames) {
            try {
                repository.save(game);
            } catch (DataIntegrityViolationException e) {
                System.out.println("Game " + game.getId() + " already exists in database.");
            }
        }

        System.out.println("Game Database updated from API.");
        System.out.println("Game Database contents:");
        for (Game game : repository.findAll()) {
            System.out.println(game);
        }
    }
}