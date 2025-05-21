package edu.sdccd.cisc191.Server;

import edu.sdccd.cisc191.Common.Models.Game;
import edu.sdccd.cisc191.Server.repositories.GameRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.net.*;
import java.io.*;

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
 */
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
     * @param args Command-line arguments (not used in this application).
     */

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = null;

        GameDatabase.main(args);

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
                ClientHandler clientSocket = new ClientHandler(client);
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
