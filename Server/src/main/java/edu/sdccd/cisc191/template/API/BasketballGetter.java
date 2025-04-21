package edu.sdccd.cisc191.template.API;

public class BasketballGetter extends APIGetter {
    public BasketballGetter() {
        apiURL = "https://v1.basketball.api-sports.io/games?date=";
        leagueName = "NBA";
    }
}
