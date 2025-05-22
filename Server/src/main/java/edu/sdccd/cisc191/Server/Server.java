package edu.sdccd.cisc191.Server;

import edu.sdccd.cisc191.Common.Models.Game;
import edu.sdccd.cisc191.Common.Models.User;
import edu.sdccd.cisc191.Server.repositories.UserRepository;
import edu.sdccd.cisc191.server.BetDatabase;
import edu.sdccd.cisc191.Server.repositories.GameRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.FluentQuery;

import java.net.*;
import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * A multi-threaded server that listens for connection requests on a specified port
 * and handles each client connection in a separate thread.
 *
 *  The server sends the current time to connected clients and continues running
 * indefinitely until it is terminated manually. It uses the {@link ClientHandler}
 * class to process individual client connections.
 *
 * @version 1.0.0
 * @author Andy Ly
 * @see ClientHandler
 * */

@SpringBootApplication
@EnableJpaRepositories("edu.sdccd.cisc191.Server.repositories")
@EntityScan(basePackages = {"edu.sdccd.cisc191.Common.Models"})
@ComponentScan(basePackages = {"edu.sdccd.cisc191.Server.controllers", "edu.sdccd.cisc191.Server.repositories"})
public class Server {

    /**
     * The entry point of the server application. Sets up the server to listen on
     * port 4444, accepts client connections, and delegates processing to
     * ClientHandler instances running in separate threads.
     *
     * @param args Command-line arguments (not used in this application). **/

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = null;
        GameRepository gameRepository = new GameRepository() {
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
        UserRepository userRepository = new UserRepository() {
            @Override
            public List<User> findAll(Sort sort) {
                return List.of();
            }

            @Override
            public Page<User> findAll(Pageable pageable) {
                return null;
            }

            @Override
            public <S extends User> S save(S entity) {
                return null;
            }

            @Override
            public <S extends User> List<S> saveAll(Iterable<S> entities) {
                return List.of();
            }

            @Override
            public Optional<User> findById(Long aLong) {
                return Optional.empty();
            }

            @Override
            public boolean existsById(Long aLong) {
                return false;
            }

            @Override
            public List<User> findAll() {
                return List.of();
            }

            @Override
            public List<User> findAllById(Iterable<Long> longs) {
                return List.of();
            }

            @Override
            public long count() {
                return 0;
            }

            @Override
            public void deleteById(Long aLong) {

            }

            @Override
            public void delete(User entity) {

            }

            @Override
            public void deleteAllById(Iterable<? extends Long> longs) {

            }

            @Override
            public void deleteAll(Iterable<? extends User> entities) {

            }

            @Override
            public void deleteAll() {

            }

            @Override
            public void flush() {

            }

            @Override
            public <S extends User> S saveAndFlush(S entity) {
                return null;
            }

            @Override
            public <S extends User> List<S> saveAllAndFlush(Iterable<S> entities) {
                return List.of();
            }

            @Override
            public void deleteAllInBatch(Iterable<User> entities) {

            }

            @Override
            public void deleteAllByIdInBatch(Iterable<Long> longs) {

            }

            @Override
            public void deleteAllInBatch() {

            }

            @Override
            public User getOne(Long aLong) {
                return null;
            }

            @Override
            public User getById(Long aLong) {
                return null;
            }

            @Override
            public User getReferenceById(Long aLong) {
                return null;
            }

            @Override
            public <S extends User> Optional<S> findOne(Example<S> example) {
                return Optional.empty();
            }

            @Override
            public <S extends User> List<S> findAll(Example<S> example) {
                return List.of();
            }

            @Override
            public <S extends User> List<S> findAll(Example<S> example, Sort sort) {
                return List.of();
            }

            @Override
            public <S extends User> Page<S> findAll(Example<S> example, Pageable pageable) {
                return null;
            }

            @Override
            public <S extends User> long count(Example<S> example) {
                return 0;
            }

            @Override
            public <S extends User> boolean exists(Example<S> example) {
                return false;
            }

            @Override
            public <S extends User, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
                return null;
            }

            @Override
            public User findByName(String name) {
                return null;
            }
        };
        BetDatabase betDatabase = new BetDatabase(userRepository, gameRepository);
        BetDatabase.main(args);

         try {
            // Initialize the server to listen on port 4444 with a backlog of 4096.
            serverSocket = new ServerSocket(4444, 4096);

            System.out.println("Server started on port 4444");

            // Enable address reuse to allow multiple connections from the same host.
            serverSocket.setReuseAddress(true);

            // Continuously wait for client connections.
            while (true) {
                Socket client = serverSocket.accept();

                // Log the new client connection.
                System.out.println("New client connected: " + client.getInetAddress().getHostAddress());

                // Handle the client connection in a separate thread.
                ClientHandler clientSocket = new ClientHandler(client, betDatabase);
                new Thread(clientSocket).start();
            }
        } catch (IOException e) {
            // Print any exceptions that occur during server operation.
            e.printStackTrace();
        } finally {
            // Ensure the server socket is closed when the server terminates.
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}