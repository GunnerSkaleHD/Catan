package org.example.catan;

import java.util.ArrayList;
import java.util.List;


public class TradeManager {
    private final List<Trade> activeTrades = new ArrayList<>();

    public void addTrade(Trade trade) {
        activeTrades.add(trade);
    }

    public List<Trade> getActiveTrades() {
        return new ArrayList<>(activeTrades);  
    }

    public void clearAllTrades() {
        activeTrades.clear();
    }

    public boolean hasActiveTrades() {
        return !activeTrades.isEmpty();
    }
}







