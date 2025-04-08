package edu.sdccd.cisc191.template;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Handles client requests in a separate thread. Each instance of  ClientHandler
 * is responsible for processing the requests of a single client connected to the server.
 *
 *  It supports operations such as retrieving game or user details, updating user information,
 * and managing bets. Communication is facilitated through JSON-encoded requests and responses.
 *
 * @author Andy Ly
 * @see Server
 * @see Request
 */
class ClientHandler implements Runnable {

    private ServerSocket serverSocket; // Server socket for incoming connections
    private Socket clientSocket; // Socket for communicating with the client
    private PrintWriter out; // Output stream to send responses to the client
    private BufferedReader in; // Input stream to receive requests from the client

    /**
     * Creates a new  ClientHandler  for a given client socket.
     *
     * @param socket The client socket to be handled.
     */
    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    /**
     * Executes the thread to handle client communication.
     * Processes incoming JSON-encoded requests, determines their type,
     * and routes them to the appropriate handler methods.
     */
    @Override
    public void run() {
        System.out.println("Passed duties on to ClientHandler...");

        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                Request request = Request.fromJSON(inputLine); // Deserialize the client request
                System.out.println(request.toString()); // Log the request for debugging

                // Handle request types
                if (Objects.equals(request.getRequestType(), "GetSize")) {
                    String response = "-1";
                    if (request.getId() == 1) {
                        response = GameDatabase.getInstance().getSize();
                    } else if (request.getId() == 2) {
                        response = UserDatabase.getInstance().getSize();
                    }
                    out.println(response);
                } else if (Objects.equals(request.getRequestType(), "Game")) {
                    Game response = null;
                    if (request.getId() >= 0) {
                        response = getGame(request);
                    }
                    out.println(Game.toJSON(response));
                } else if (Objects.equals(request.getRequestType(), "User")) {
                    User response = null;
                    if (request.getId() >= 0) {
                        response = getUser(request);
                    }
                    out.println(User.toJSON(response));
                } else if (Objects.equals(request.getRequestType(), "ModifyUser")) {
                    User response = null;
                    if (request.getId() >= 0) {
                        response = handleModifyUserRequest(request);
                    }
                    out.println(User.toJSON(response));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Retrieves the game associated with the given request ID.
     *
     * @param request The client request containing the game ID.
     * @return The  Game  object corresponding to the ID, or null if not found.
     */
    private static Game getGame(Request request) {
        Game response;

        List<Game> gameDatabase = GameDatabase.getInstance().getGameDatabase();
        if (request.getId() >= gameDatabase.size()) {
            response = null;
        } else {
            response = gameDatabase.get(request.getId());
        }
        return response;
    }

    /**
     * Retrieves the user associated with the given request ID.
     *
     * @param request The client request containing the user ID.
     * @return The  User  object corresponding to the ID, or null if not found.
     */
    private static User getUser(Request request) {
        User response;

        List<User> userDatabase = UserDatabase.getInstance().getUserDatabase();
        if (request.getId() >= userDatabase.size()) {
            response = null;
        } else {
            response = userDatabase.get(request.getId());
        }
        return response;
    }

    /**
     * Modifies the attributes of a user based on the given request.
     *
     *  This method is synchronized to ensure thread safety when multiple clients
     * modify the user database concurrently.
     *
     * @param request The client request containing details of the modifications.
     * @return The modified  User  object.
     * @throws Exception If an error occurs during modification.
     */
    private static synchronized User handleModifyUserRequest(Request request) throws Exception {
        UserDatabase db = UserDatabase.getInstance();
        List<User> userDatabase = db.getUserDatabase();

        User userToModify = userDatabase.get(request.getId());

        // Update user attributes based on the request
        Map<String, Object> attributes = request.getAttributesToModify();
        if (attributes.containsKey("Name")) {
            userToModify.setName((String) attributes.get("Name"));
        }
        if (attributes.containsKey("Money")) {
            userToModify.setMoney((Integer) attributes.get("Money"));
        }

        if (attributes.containsKey("Increment Money")) {
            userToModify.incrMoney((Integer) attributes.get("Increment Money"));
        }

        if (attributes.containsKey("Decrement Money")) {
            userToModify.incrMoney((Integer) attributes.get("Decrement Money"));
        }

        if (attributes.containsKey("addBet")) {
            userToModify.addBet(Bet.fromJSON((String) attributes.get("addBet")));
        }
        if (attributes.containsKey("removeBet")) {
            userToModify.removeBet(Bet.fromJSON((String) attributes.get("removeBet")));
        }

        db.saveToFile();

        return userToModify;
    }
}
