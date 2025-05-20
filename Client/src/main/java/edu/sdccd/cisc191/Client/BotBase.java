package edu.sdccd.cisc191.Client;

import edu.sdccd.cisc191.Common.Models.Game;
import edu.sdccd.cisc191.Common.Request;
import edu.sdccd.cisc191.Common.Bet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * BotBase simulates automated betting bots that place bets every 2–3 minutes.
 */
public class BotBase {
    private Timer timer;
    private Random random;
    ArrayList<Game> basketballGames = new ArrayList<>();

    // TODO: create a networking utility object that contains all of the methods for talking to the server, so we don't have to copy them between Client and Bot classes
    /**
     * The socket used to connect to the server.
     */
    private Socket clientSocket;

    private ObjectOutputStream out; // The output stream for sending requests to the server.
    private ObjectInputStream in; // The input stream for receiving responses from the server.

    public BotBase() throws Exception {
        // 1) open socket *before* you ever send a request
        startConnection("localhost", 4444);

        // 2) set up your timer & randomness
        timer  = new Timer();
        random = new Random();

        // 3) now you can getBasketballGames() safely inside startBot()
        startBot();

    }
    // --- Socket and Request Methods ---
    /**
     * Establishes a connection to the server using the provided IP address and port.
     *
     * @param ip   the IP address of the server.
     * @param port the port number on the server.
     * @throws IOException if an I/O error occurs when opening the connection.
     */
    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);

        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
    }


    // Update stopConnection to check for null before closing resources:
    public void stopConnection() throws IOException {
        if (in != null) {
            in.close();
        }
        if (out != null) {
            out.close();
        }
        if (clientSocket != null) {
            clientSocket.close();
        }
    }

    /**
     * Sends a request to the server and returns a response of the expected type.
     *
     * Never call this method directly, call one of its wrappers for safe usage.
     *
     * @param <T>         the type parameter corresponding to the expected response type.
     * @return the response from the server cast to the specified type.
     * @throws Exception if an error occurs during the request.
     */
    private <T> T sendRequest(Request request, Class<T> responseType) throws Exception {
        // Write request
        out.writeObject(request);
        out.flush();

        // read back whatever the server sent
        Object raw = in.readObject();
        System.out.println("Raw: " + raw);
        System.out.println("Raw type: " + raw.getClass());
        System.out.println("Response Type: " + responseType);

        // cast into the expected type

        try {
            return responseType.cast(raw);
        }
        catch (ClassCastException e) {
            System.out.println("ClassCastException, could not cast " + raw.getClass() + " to " + responseType);
        }

        return null;

    }

    /**
     * Retrieves a game object from the server by the specified ID.
     *
     * @param id the identifier of the game to retrieve.
     * @return the Game object if found; null otherwise.
     * @throws IOException if an I/O error occurs during the request.
     */
    public Game getRequest(int id, String type) throws IOException {
        try {
            this.startConnection("localhost", 4444);

            // build a request object
            Request req = new Request("Game", id);

            return this.sendRequest(req, Game.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                this.stopConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private ArrayList<Game> getBasketballGames() throws Exception {
        ArrayList<Game> basketballGames;
        basketballGames = sendRequest(new Request("Basketball", 1), ArrayList.class);
        return basketballGames;
    }


    // Starts the bot betting loop
    private void startBot() throws Exception {
        basketballGames = this.getBasketballGames();
        scheduleNextBet();
    }

    // Schedules the next bet to happen in 2–3 minutes
    private void scheduleNextBet() {
        int delay = (2 + random.nextInt(2)) * 60 * 1000; // 2–3 minutes in milliseconds
        System.out.println("Bet in " + delay);
        int index = (int)(Math.random() * basketballGames.size());
        Game gameToBet = basketballGames.get(index);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                placeRandomBet(gameToBet);
                scheduleNextBet(); // Schedule again after this bet
            }
        }, delay);
    }

    // Simulates placing a random bet
    private void placeRandomBet(Game game) {
        double betAmt = 10 + random.nextInt(91); // $10 to $100

        int teamSelect = (Math.random() <= 0.5) ? 1 : 2;
        String team;

        if (teamSelect == 1) team = game.getTeam1(); else team = game.getTeam2();

        // Create a new Bet object using the updated constructor, casting the double to int
        Bet newBet = new Bet(game, (int) betAmt, team);
        System.out.println("Bot placed bet: " + newBet);
    }

    public static void main(String[] args) {
            try {
                BotBase bot = new BotBase();
            } catch (Exception e) {
                e.printStackTrace();
        }
    }
}