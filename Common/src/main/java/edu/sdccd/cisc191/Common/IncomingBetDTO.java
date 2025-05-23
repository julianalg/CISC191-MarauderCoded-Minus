package edu.sdccd.cisc191.Common;

public class IncomingBetDTO {
    private Long gameId;
    private String betTeam;
    private int betAmt;

    public IncomingBetDTO(Long gameId, String betTeam, int betAmt) {
        this.gameId = gameId;
        this.betTeam = betTeam;
        this.betAmt = betAmt;
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


    // getters + setters
}
