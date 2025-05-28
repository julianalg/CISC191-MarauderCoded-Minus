package edu.sdccd.cisc191.Server;

import edu.sdccd.cisc191.Common.Models.Game;
import edu.sdccd.cisc191.Server.API.BaseballGetter;
import edu.sdccd.cisc191.Server.API.BasketballGetter;
import edu.sdccd.cisc191.Server.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * A Spring-managed component that extends GenericDatabase to manage Game entities.
 * This class handles loading, initializing, saving, and updating the game database using API data.
 * It supports both baseball and basketball games via separate API getter classes.
 */
@Component
public class GameDatabase extends GenericDatabase<Game, Long, GameRepository> {

    /**
     * Constructs the GameDatabase, loads the existing database from a file,
     * initializes it if necessary, and updates it with new data from external APIs.
     *
     * @param gameRepository the repository for Game entities
     * @throws Exception if loading or initialization fails
     */
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

    /**
     * Provides a default initialization for the Game database if no data is found.
     * Currently adds an empty list, but can be extended for actual default data.
     *
     * @throws Exception if initialization fails
     */
    @Override
    protected void initializeDefaultEntities() throws Exception {
        List<Game> defaultGames = new ArrayList<>();
        repository.saveAll(defaultGames);
    }

    /**
     * Specifies the file name used for saving and loading game data.
     *
     * @return the JSON file name for storing games
     */
    @Override
    protected String getFileName() {
        return "games.json";
    }

    /**
     * Provides the name of the entity being managed.
     *
     * @return the string "Game"
     */
    @Override
    protected String getEntityName() {
        return "Game";
    }

    /**
     * Updates the database with new game data fetched from Baseball and Basketball APIs.
     * Skips any games that already exist (identified by ID) to avoid duplicates.
     *
     * @throws Exception if an API call or database operation fails
     */
    public void updateDatabaseFromAPI() throws Exception {
        BaseballGetter baseballGetter = new BaseballGetter();
        ArrayList<Game> baseballGames = baseballGetter.getGames("Baseball");

        System.out.println("Found " + baseballGames.size() + " new baseball games.");
        for (Game game : baseballGames) {
            try {
                repository.save(game);
            } catch (DataIntegrityViolationException e) {
                System.out.println("Game " + game.getId() + " already exists in database.");
            }
        }

        BasketballGetter basketballGetter = new BasketballGetter();
        ArrayList<Game> basketballGames = basketballGetter.getGames("Basketball");

        System.out.println("Found " + basketballGames.size() + " new basketball games.");
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
