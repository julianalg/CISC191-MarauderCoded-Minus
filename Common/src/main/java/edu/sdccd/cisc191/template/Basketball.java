package edu.sdccd.cisc191.template;

import java.util.Date;

public class Basketball extends Game {
    String team1;
    String team2;

    public Basketball(String format, String format1, Date date, Date randomDate) {
    }

    public Basketball(String team1, String team2) {
        this.team1 = team1;
        this.team2 = team2;
    }

    @Override
    public String toString() {
        return team1 + " " + team2;
    }



    @Override
    public String getGameType() {
        return "basketball";
    }

}
