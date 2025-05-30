package edu.sdccd.cisc191.Server.repositories;

import edu.sdccd.cisc191.Common.GameBST;
import edu.sdccd.cisc191.Common.Models.Game;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.joda.time.DateTime;
import java.util.stream.Collectors;

@Repository
public class GameRepositoryImpl extends SimpleJpaRepository<Game, Long> implements GameRepository {
    private GameBST.BinarySearchTree<Game> idTree;
    private GameBST.BinarySearchTree<Game> team1OddsTree;
    private GameBST.BinarySearchTree<Game> team2OddsTree;
    private GameBST.BinarySearchTree<Game> dateTree;

    public GameRepositoryImpl(EntityManager em) {
        super(Game.class, em);
    }

    private void initializeTrees() {
        List<Game> allGames = super.findAll();
        idTree = GameBST.buildGameIdTree(allGames);
        team1OddsTree = GameBST.buildOddsTree(allGames);
        team2OddsTree = GameBST.buildTeam2OddsTree(allGames);
        dateTree = GameBST.buildDateTree(allGames);

    }

    @Override
    public Game save(Game game) {
        Game savedGame = super.save(game);
        initializeTrees(); // Rebuild trees when data changes
        return savedGame;
    }

    @Override
    public List<Game> findAll() {
        if (idTree == null) {
            initializeTrees();
        }
        return idTree.inorderTraversal();
    }

    @Override
    public List<Game> findAllSortedByTeam1Odds() {
        if (team1OddsTree == null) {
            initializeTrees();
        }
        return team1OddsTree.inorderTraversal();
    }

    @Override
    public List<Game> findAllSortedByTeam2Odds() {
        if (team2OddsTree == null) {
            initializeTrees();
        }
        return team2OddsTree.inorderTraversal();
    }

    @Override
    public Optional<Game> findByIdUsingBST(Long id) {
        if (idTree == null) {
            initializeTrees();
        }
        return idTree.inorderTraversal().stream()
                .filter(game -> game.getId() == id)
                .findFirst();
    }

    public List<Game> findGamesByDateRange(DateTime start, DateTime end) {
        if (dateTree == null) {
            initializeTrees();
        }
        return dateTree.inorderTraversal().stream()
                .filter(game -> game.getGameDate().isAfter(start) 
                    && game.getGameDate().isBefore(end))
                .collect(Collectors.toList());
    }

    public List<Game> findAllSortedByDate() {
        if (dateTree == null) {
            initializeTrees();
        }
        return dateTree.inorderTraversal();
    }
}