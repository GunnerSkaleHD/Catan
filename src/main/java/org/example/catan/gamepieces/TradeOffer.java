package org.example.catan.gamepieces;

import lombok.Getter;

import java.util.Map;




/**
 * Represents a trade offer made by a player.
 * Can be a player-to-player trade or a trade with the bank.
 */
@Getter
public class TradeOffer {
    private final Player sender;
    private final Map<Resources, Integer> offer;     // What the sender gives
    private final Map<Resources, Integer> request;   // What the sender wants
    private final boolean isBankTrade;

    private boolean accepted = false;
    private Player acceptedBy = null;

    public TradeOffer(Player sender,
                      Map<Resources, Integer> offer,
                      Map<Resources, Integer> request,
                      boolean isBankTrade) {
        this.sender = sender;
        this.offer = offer;
        this.request = request;
        this.isBankTrade = isBankTrade;
    }

    /**
     * Marks this trade offer as accepted by a player.
     * @param accepter The player who accepted the trade
     */
    public void accept(Player accepter) {
        this.accepted = true;
        this.acceptedBy = accepter;
    }

    /**
     * Determines if the trade offer should expire (e.g., when the sender's turn comes again).
     * @param currentTurnPlayer The player whose turn it is now
     * @return true if the trade offer is expired
     */
    public boolean isExpired(Player currentTurnPlayer) {
        return sender.equals(currentTurnPlayer);
    }

    /**
     * Getter method to check if this is a bank trade.
     * Required for controller logic.
     */
    public boolean isBankTrade() {
        return isBankTrade;
    }
}


