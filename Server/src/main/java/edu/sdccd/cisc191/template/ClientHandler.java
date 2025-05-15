package edu.sdccd.cisc191.template;

import edu.sdccd.cisc191.template.API.BasketballGetter;
import edu.sdccd.cisc191.template.API.BaseballGetter;

import java.io.*;
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
    ObjectOutputStream out; // Output stream to send responses to the client
    ObjectInputStream  in; // Input stream to receive requests from the client

    /**
     * Creates a new  ClientHandler  for a given client socket.
     *
     * @param socket The client socket to be handled.
     */
    public ClientHandler(Socket socket) throws IOException {
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
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());

            while (true) {
                // Block until the client sends a Request object
                Object obj = in.readObject();
                System.out.println("Object Received: \n" + obj);
                if(!(obj instanceof Request)) {
                    System.err.println("Unexpected type: " + obj.getClass().getName());
                    break;
                }
                Request request = (Request) obj;
                System.out.println("Client received: " + request);

                // Handle the request
                Object response;
                switch (request.getRequestType()) {
                    case "GetSize":
                        response = (request.getId() == 1) ? GameDatabase.getInstance().getSize() : UserDatabase.getInstance().getSize();
                        break;
                    case "Game":
                        response = (request.getId() >= 0) ? getGame(request) : null;
                        break;
                    case "User":
                        response = (request.getId() >= 0) ? getUser(request) : null;
                        break;
                    case "ModifyUser":
                        response = (request.getId() >= 0) ? handleModifyUserRequest(request) : null;
                        break;
                    case "Basketball":
                        response = (request.getId() >= 0) ? getBasketball(request) : new ArrayList<Game>();
                        break;
                    case "Baseball":
                        response = (request.getId() >= 0) ? getBaseball(request) : new ArrayList<Game>();
                        break;
                    case "Bet":
                        response = (request.getId() >= 0) ? getBet(request) : null;
                        break;
                    default:
                        response = new IllegalArgumentException("Unknown request type");
                }

                // Send it back
                out.writeObject(response);
                out.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                clientSocket.close();
            } catch (IOException ignored) {}
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

    private static ArrayList<Game> getBasketball(Request request) throws Exception {
        BasketballGetter a = new BasketballGetter();
        return a.getGames("Basketball");
    }

    private static ArrayList<Game> getBaseball(Request request) throws Exception {
        BaseballGetter a = new BaseballGetter();
        return a.getGames("Baseball");
    }

    private static long getBet(Request request) throws Exception {
        
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
