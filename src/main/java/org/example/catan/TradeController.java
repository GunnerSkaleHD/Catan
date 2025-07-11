package org.example.catan;

// ✅ HIER kommen die Imports hin:
import org.example.catan.Trade;
import org.example.catan.TradeManager;
import org.example.catan.Resources;
import org.example.catan.Player;

import java.util.List;

public class TradeController {
    private final List<Player> players;
    private final TradeManager tradeManager;

    public TradeController(List<Player> players, TradeManager tradeManager) {
        this.players = players;
        this.tradeManager = tradeManager;
    }

    public void submitTrade(Player from, Player to, String offerRes, int offerAmt, String wantRes, int wantAmt) {
        if (from.getResourceCount(Resources.valueOf(offerRes)) >= offerAmt) {
            Trade trade = new Trade(from, offerRes, offerAmt, wantRes, wantAmt, to);
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


 
