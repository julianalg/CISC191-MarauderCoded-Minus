package edu.sdccd.cisc191.Server;

// This class is a proof of concept class to see that the JPA database is working
// Eventually I will merge the RestController into userDatabase.java
// and merge the Springboot Application(this class) into Server.java
// This is based on the Andrew Huang repo

import edu.sdccd.cisc191.Common.Models.Game;
import edu.sdccd.cisc191.Server.API.BaseballGetter;
import edu.sdccd.cisc191.Server.API.BasketballGetter;
import edu.sdccd.cisc191.Server.repositories.GameRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.FluentQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;


@SpringBootApplication
@EnableJpaRepositories("edu.sdccd.cisc191.Server.repositories")
@EntityScan(basePackages = {"edu.sdccd.cisc191.Common.Models"}) 
@ComponentScan(basePackages = {"edu.sdccd.cisc191.Server.controllers", "edu.sdccd.cisc191.Server.repositories"})
public class JPARunGameTest implements CommandLineRunner  {
    private final GameRepository gameRepository = new GameRepository() {
        @Override
        public void flush() {

        }

        @Override
        public <S extends Game> S saveAndFlush(S entity) {
            return null;
        }

        @Override
        public <S extends Game> List<S> saveAllAndFlush(Iterable<S> entities) {
            return List.of();
        }

        @Override
        public void deleteAllInBatch(Iterable<Game> entities) {

        }

        @Override
        public void deleteAllByIdInBatch(Iterable<Long> longs) {

        }

        @Override
        public void deleteAllInBatch() {

        }

        @Override
        public Game getOne(Long aLong) {
            return null;
        }

        @Override
        public Game getById(Long aLong) {
            return null;
        }

        @Override
        public Game getReferenceById(Long aLong) {
            return null;
        }

        @Override
        public <S extends Game> List<S> findAll(Example<S> example) {
            return List.of();
        }

        @Override
        public <S extends Game> List<S> findAll(Example<S> example, Sort sort) {
            return List.of();
        }

        @Override
        public <S extends Game> List<S> saveAll(Iterable<S> entities) {
            return List.of();
        }

        @Override
        public List<Game> findAll() {
            return List.of();
        }

        @Override
        public List<Game> findAllById(Iterable<Long> longs) {
            return List.of();
        }

        @Override
        public <S extends Game> S save(S entity) {
            return null;
        }

        @Override
        public Optional<Game> findById(Long aLong) {
            return Optional.empty();
        }

        @Override
        public boolean existsById(Long aLong) {
            return false;
        }

        @Override
        public long count() {
            return 0;
        }

        @Override
        public void deleteById(Long aLong) {

        }

        @Override
        public void delete(Game entity) {

        }

        @Override
        public void deleteAllById(Iterable<? extends Long> longs) {

        }

        @Override
        public void deleteAll(Iterable<? extends Game> entities) {

        }

        @Override
        public void deleteAll() {

        }

        @Override
        public List<Game> findAll(Sort sort) {
            return List.of();
        }

        @Override
        public Page<Game> findAll(Pageable pageable) {
            return null;
        }

        @Override
        public <S extends Game> Optional<S> findOne(Example<S> example) {
            return Optional.empty();
        }

        @Override
        public <S extends Game> Page<S> findAll(Example<S> example, Pageable pageable) {
            return null;
        }

        @Override
        public <S extends Game> long count(Example<S> example) {
            return 0;
        }

        @Override
        public <S extends Game> boolean exists(Example<S> example) {
            return false;
        }

        @Override
        public <S extends Game, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
            return null;
        }
    };

        public static void main(String[] args) {
            SpringApplication.run(JPARunGameTest.class, args);
        }


        public void run(String... args) throws Exception {
            BaseballGetter baseballGetter = new BaseballGetter();
            ArrayList<Game> games = baseballGetter.getGames("Baseball");
            System.out.println("Total games in database: " + games.size());

            for (Game game : games) {
                System.out.println("Adding game " + game.getId() + " to database");
                gameRepository.save(game);
            }
        }
    }

