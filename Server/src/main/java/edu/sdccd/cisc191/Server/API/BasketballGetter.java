package edu.sdccd.cisc191.Server.API;


public class BasketballGetter extends APIGetter {
    public BasketballGetter() {
        apiURL = "https://v1.basketball.api-sports.io/";
        leagueName = "WNBA";
    }

    public static void main(String[] args) throws Exception {
//        BasketballGetter basketballGetter = new BasketballGetter();
//        System.out.println(basketballGetter.getGames("Basketball"));
        BasketballGetter basketballGetter = new BasketballGetter();
        basketballGetter.getOdd(163994);
    }


}
