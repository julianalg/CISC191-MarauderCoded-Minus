package edu.sdccd.cisc191.Client;

import edu.sdccd.cisc191.Common.Models.Game;
import edu.sdccd.cisc191.Common.Models.Bet;

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
    ArrayList<Game> allGames = new ArrayList<>();


    public BotBase() throws Exception {
        // 2) set up your timer & randomness
        timer  = new Timer();
        random = new Random();

        // 3) now you can getBasketballGames() safely inside startBot()
        startBot();

    }

    // Starts the bot betting loop
    private void startBot() throws Exception {
        allGames = Client.getGames();
        scheduleNextBet();
    }

    // Schedules the next bet to happen in 2–3 minutes
    private void scheduleNextBet() {
        int delay = (2 + random.nextInt(2)) * 60; // 2–3 minutes in milliseconds
        System.out.println("Bet in " + delay);
        int index = (int)(Math.random() * allGames.size());
        Game gameToBet = allGames.get(index);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    placeRandomBet(gameToBet);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                scheduleNextBet(); // Schedule again after this bet
            }
        }, delay);
    }

    // Simulates placing a random bet
    private void placeRandomBet(Game game) throws IOException, InterruptedException {
        double betAmt = 10 + random.nextInt(91); // $10 to $100

        int teamSelect = (Math.random() <= 0.5) ? 1 : 2;
        String team;

        if (teamSelect == 1) team = game.getTeam1(); else team = game.getTeam2();

        // Get odds
        double odds = Client.getOdds((int) game.getId(), game.getSport(), (teamSelect == 1 ? 0 : 1));


        // Create a new Bet object using the updated constructor, casting the double to int
        Bet newBet = new Bet(game, (int) betAmt, team, (int) (betAmt * odds));
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