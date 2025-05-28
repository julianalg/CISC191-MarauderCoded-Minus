package edu.sdccd.cisc191.Common;

import lombok.Data;

/**
 * Data Transfer Object (DTO) representing an incoming bet.
 * <p>
 * This class is used to encapsulate information about a user's bet request,
 * including the ID of the game being bet on, the team being bet on,
 * the amount of the bet, and the potential win amount.
 * </p>
 *
 * <p>
 * Lombok's {@code @Data} annotation automatically generates boilerplate code
 * such as getters, setters, {@code toString()}, {@code equals()}, and {@code hashCode()} methods.
 * </p>
 */
@Data
public class IncomingBetDTO {

    /**
     * The ID of the game that the bet is placed on.
     */
    private Long gameId;

    /**
     * The name or identifier of the team that the user is betting on.
     */
    private String betTeam;

    /**
     * The amount of money the user is betting.
     */
    private int betAmt;

    /**
     * The potential amount the user can win if the bet is successful.
     */
    private int winAmt;

    /**
     * Constructs a new {@code IncomingBetDTO} with the specified values.
     *
     * @param gameId  the ID of the game
     * @param betTeam the team being bet on
     * @param betAmt  the amount being bet
     * @param winAmt  the potential winnings
     */
    public IncomingBetDTO(Long gameId, String betTeam, int betAmt, int winAmt) {
        this.gameId = gameId;
        this.betTeam = betTeam;
        this.betAmt = betAmt;
        this.winAmt = winAmt;
    }
}
