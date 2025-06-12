// Different dev cards?? Text and amount and name
package org.example.catan;

import java.util.*;
public class Bank {
    private Map<String, Integer> resources;
    private List<Development> developmentCards;

    // nested map for three arguments: type, color and amount
    private Map<String, Map<String, Integer>> infrastructure;

    public Bank (int wood, int sheep, int wheat, int bricks, int stone) {
       // each resource needs an amount
        resources = new HashMap<>();
        resources.put("wood", 19);
        resources.put("sheep", 19);
        resources.put("wheat", 19);
        resources.put("bricks", 19);
        resources.put("stone", 19);

        infrastructure = new HashMap<>();
        Map<String, Integer> amountRoads = new HashMap<>();
        Map<String, Integer> amountSettlements = new HashMap<>();
        Map<String, Integer> amountCities = new HashMap<>();

        amountRoads.put("white", 15);
        amountRoads.put("orange", 15);
        amountRoads.put("red", 15);
        amountRoads.put("blue", 15);

        amountSettlements.put("white", 5);
        amountSettlements.put("orange", 5);
        amountSettlements.put("red", 5);
        amountSettlements.put("blue", 5);

        amountCities.put("white", 4);
        amountCities.put("orange", 4);
        amountCities.put("red", 4);
        amountCities.put("blue", 4);

        infrastructure.put("road", amountRoads);
        infrastructure.put("settlement", amountSettlements);
        infrastructure.put("city", amountCities);
       
        
       manageDevelopmentCards();
    }

    private void manageDevelopmentCards() {
        developmentCards = new ArrayList<>();

        // 2 Monopoly cards
        for(int i=0; i<2; i++) {
            developmentCards.add(new Development("Monopoly", "Announce one type of resource. Each player must give you all their resource cards of that type."));
        }

        // 2 road building cards
        for (int i=0; i< 2; i++) {
            developmentCards.add(new Development("Road Building", "Build 2 roads at no cost."));
        }

        // 2 invention cards
        for (int i=0; i<2; i++) {
            developmentCards.add(new Development("Invention", "Take any 2 resource cards from the supply."));
        }

        // 14 knight cards
        for (int i=0; i<14; i++) {
            developmentCards.add(new Development("Knight", "Move the robber to a new hex. Steal 1 random resource card from a player with a building on that hex."));
        }

        // 5 Victory Point cards
        for (int i=0; i < 5; i++) {
            developmentCards.add(new Development("Victory Point", "Play any number of Victory Point cards (even on the turn you build them) to win the game."));
        }

        //Randomly shuffle the deck
        Collections.shuffle(developmentCards);

    }

    public List<Development> getDevelopmentCards(){
        return Collections.unmodifiableList(developmentCards);
    }


public Development buyDevelopmentCard(Player player) {
    if (resources.get("wheat") >= 1 && resources.get("sheep") >= 1 && resources.get("stone") >= 1 && !developmentCards.isEmpty()) {
        changeBankResource("wheat", -1);
        changeBankResource("sheep", -1);
        changeBankResource("stone", -1);

        Development card = developmentCards.remove(0);
        player.addDevelopmentCard(card);
        return card;
    }
    else {
        System.out.println("Not enough resources or no development cards left");
        return null;
    }
}

    // plus is the default
private void changeBankResource(String resource, int amount) {
    resources.put(resource, resources.get(resource) + amount);
}
public boolean spendResources(Player player, String resource, int amount) {
    if (amount <= 0) {
        return false;
    }
    boolean success = player.removeResource(resource, amount);
    if (success) {
        switch (resource.toLowerCase()) {
            case "wood" -> changeBankResource("wood", amount);
            case "sheep" -> changeBankResource("sheep", amount);
            case "wheat" -> changeBankResource("wheat", amount);
            case "bricks" -> changeBankResource("bricks", amount);
            case "stone" -> changeBankResource("stone", amount);
            default -> {
                System.out.println(resource + " is unkown");
                return false;
            }
        }
        return true;
    }
    return false;
}

public boolean canBuildRoad(Player player) {
    String color = player.getColor();
    int roadsLeft = infrastructure.get("road").get(color);
    boolean enoughWood = player.hasResource("wood", 1);
    boolean enoughBricks = player.hasResource("bricks", 1);

    return enoughBricks && enoughWood && roadsLeft > 0;
    }

public void buildRoad (Player player) {
    String color = player.getColor();
    if (canBuildRoad(player)) {
        player.removeResource("wood", 1);
        player.removeResource("bricks", 1);
        changeBankResource("wood", 1);
        changeBankResource("bricks", 1);
        infrastructure.get("road").put(color, infrastructure.get("road").get(color) - 1);
    }
}

public boolean canBuildSettlement(Player player) {
    String color = player.getColor();
    int settlementsLeft = infrastructure.get("settlement").get(color);
    boolean enoughWood = player.hasResource("wood", 1);
    boolean enoughBricks = player.hasResource("bricks", 1);
    boolean enoughSheep = player.hasResource("sheep", 1);
    boolean enoughWheat = player.hasResource("wheat", 1);

    return enoughWood && enoughBricks && enoughSheep && enoughWheat && settlementsLeft > 0;
}

public void buildSettlement (Player player) {
    String color = player.getColor();
    if (canBuildSettlement(player)) {
        player.removeResource("wood", 1);
        player.removeResource("bricks", 1);
        player.removeResource("sheep", 1);
        player.removeResource("wheat", 1);
        changeBankResource("wood", 1);
        changeBankResource("bricks", 1);
        changeBankResource("sheep", 1);
        changeBankResource("wheat", 1);
        infrastructure.get("settlement").put(color, infrastructure.get("settlement").get(color) - 1);
    }
}
// ********
public boolean canBuildCity(Player player) {
    String color = player.getColor();
    int citiesLeft = infrastructure.get("city").get(color);
    boolean enoughWheat = player.hasResource("wheat", 2);
    boolean enoughStone = player.hasResource("stone", 3);
    return enoughWheat && enoughStone && citiesLeft > 0;
}

public void buildCity(Player player){
    String color = player.getColor();
    if (canBuildCity(player)){
        player.removeResource("wheat", 2);
        player.removeResource("stone", 3);
        changeBankResource("wheat", 2);
        changeBankResource("stone", 3);
        infrastructure.get("city").put(color, infrastructure.get("city").get(color) - 1);
    }
}

}