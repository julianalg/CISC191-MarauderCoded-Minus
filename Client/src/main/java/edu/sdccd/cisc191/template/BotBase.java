package edu.sdccd.cisc191.template;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * BotBase simulates automated betting bots that place bets every 2–3 minutes.
 */
public class BotBase {
    private Timer timer;
    private Random random;

    public BotBase() {
        timer = new Timer();
        random = new Random();
        startBot();
    }

    // Starts the bot betting loop
    private void startBot() {
        scheduleNextBet();
    }

    // Schedules the next bet to happen in 2–3 minutes
    private void scheduleNextBet() {
        int delay = (2 + random.nextInt(2)) * 60 * 1000; // 2–3 minutes in milliseconds
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                placeRandomBet();
                scheduleNextBet(); // Schedule again after this bet
            }
        }, delay);
    }

    // Simulates placing a random bet
    private void placeRandomBet() {
        double betAmt = 10 + random.nextInt(91); // $10 to $100
        double winAmt = betAmt * (1.5 + random.nextDouble()); // random odds between 1.5x to 2.5x

        double[][] winOddsOvertime = new double[5][2];
        long currentTime = System.currentTimeMillis() / 1000; // current time in seconds

        for (int i = 0; i < winOddsOvertime.length; i++) {
            winOddsOvertime[i][0] = 1.5 + random.nextDouble(); // odds
            winOddsOvertime[i][1] = currentTime - (5 - i) * 60; // timestamps spaced 1 min apart
        }

        // Create a new Bet object using the updated constructor, casting the double to int
        Bet newBet = new Bet((int) betAmt, (int) winAmt, winOddsOvertime);
        System.out.println("Bot placed bet: " + newBet);
    }
}