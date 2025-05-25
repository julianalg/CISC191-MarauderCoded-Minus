package edu.sdccd.cisc191.Common;

public class IncomingBetDTO {
    private Long gameId;
    private String betTeam;
    private int betAmt;
    private int winAmt;

    public IncomingBetDTO(Long gameId, String betTeam, int betAmt, int winAmt) {
        this.gameId = gameId;
        this.betTeam = betTeam;
        this.betAmt = betAmt;
        this.winAmt = winAmt;
    }

    public int getBetAmt() {
        return betAmt;
    }

    public void setBetAmt(int betAmt) {
        this.betAmt = betAmt;
    }

    public String getBetTeam() {
        return betTeam;
    }

    public void setBetTeam(String betTeam) {
        this.betTeam = betTeam;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public int getWinAmt() {
        return winAmt;
    }

    public void setWinAmt(int winAmt) {
        this.winAmt = winAmt;
    }


    // getters + setters
}
