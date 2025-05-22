package edu.sdccd.cisc191.Server;

import edu.sdccd.cisc191.Common.Models.Game;
import edu.sdccd.cisc191.Server.API.BaseballGetter;
import edu.sdccd.cisc191.Server.API.BasketballGetter;
import edu.sdccd.cisc191.Server.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;

@Component
public class GameDatabase extends GenericDatabase<Game, Long, GameRepository> {
    
    @Autowired
    public GameDatabase(GameRepository gameRepository, 
                       @Value("${app.database.file-path-prefix}") String filePathPrefix) throws IOException {
        super(gameRepository, Game.class, filePathPrefix);
        loadOrInitializeDatabase();
    }
    
    @Override
    protected void initializeDefaultEntities() {
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
        repository.saveAll(baseballGames);

        BasketballGetter basketballGetter = new BasketballGetter();
        ArrayList<Game> basketballGames = basketballGetter.getGames("Basketball");
        repository.saveAll(basketballGames);
    }
}