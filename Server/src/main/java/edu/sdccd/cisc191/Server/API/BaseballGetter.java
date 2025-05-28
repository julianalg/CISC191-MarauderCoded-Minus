package edu.sdccd.cisc191.Server.API;


public class BaseballGetter extends APIGetter {
    public BaseballGetter() {
        apiURL = "https://v1.baseball.api-sports.io/";
        leagueName = "MLB";
    }


    public static void main(String[] args) {
        BaseballGetter baseballGetter = new BaseballGetter();
        try {
            System.out.println(baseballGetter.getGames("Baseball"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
