package org.example.catan;

import java.util.List;

public class TradeController {
    private final List<Player> players;
    private final TradeManager tradeManager;

    public TradeController(List<Player> players, TradeManager tradeManager) {
        this.players = players;
        this.tradeManager = tradeManager;
    }

    public void submitTrade(Player from, Player to, Resources offerRes, int offerAmt, Resources wantRes, int wantAmt) {
    if (from.getResourceCount(offerRes) >= offerAmt) {
        Trade trade = new Trade(from, to, offerRes, offerAmt, wantRes, wantAmt);
        tradeManager.addTrade(trade);
    }
}


    public List<Trade> getOpenTrades() {
        return tradeManager.getActiveTrades();
    }

    public void clearTrades() {
        tradeManager.clearAllTrades();
    }
}


 
