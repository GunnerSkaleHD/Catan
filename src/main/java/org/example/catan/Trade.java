package org.example.catan;

public class Trade {
    private final Player initiator;
    private final Player target;
    private final Resources offerResource;
    private final int offerAmount;
    private final Resources wantResource;
    private final int wantAmount;

    public Trade(Player initiator, Player target, Resources offerResource, int offerAmount,
                 Resources wantResource, int wantAmount) {
        this.initiator = initiator;
        this.target = target;
        this.offerResource = offerResource;
        this.offerAmount = offerAmount;
        this.wantResource = wantResource;
        this.wantAmount = wantAmount;
    }

    public Player getInitiator() {
        return initiator;
    }

    public Player getTarget() {
        return target;
    }

    public Resources getOfferResource() {
        return offerResource;
    }

    public int getOfferAmount() {
        return offerAmount;
    }

    public Resources getWantResource() {
        return wantResource;
    }

    public int getWantAmount() {
        return wantAmount;
    }

    @Override
    public String toString() {
        return initiator.getColor() + " offers " + offerAmount + " " + offerResource +
                " for " + wantAmount + " " + wantResource + " from " + target.getColor();
    }
}
