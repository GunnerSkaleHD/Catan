package org.example.catan;

import lombok.Data;
import lombok.ToString;
import org.example.catan.resources.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
public class Bank {
    public List<Resource> woodList = new ArrayList<>();
    public List<Resource> stoneList = new ArrayList<>();
    public List<Resource> brickList = new ArrayList<>();
    public List<Resource> wheatList = new ArrayList<>();
    public List<Resource> sheepList = new ArrayList<>();

    public Bank(int amountResources, int playerCount) {
        for (int i = 0; i < amountResources; i++) {
            woodList.add(new Wood());
            stoneList.add(new Stone());
            brickList.add(new Brick());
            wheatList.add(new Wheat());
            sheepList.add(new Sheep());
        }
    }

    public List<Resource> spendResources(int amount, String resourceType) {
        List<Resource> returnList = new ArrayList<>();
        switch (resourceType) {
            case "Wood":
                if (woodList.size() < amount) {
                    throw new IllegalArgumentException("Not enough wood");
                }
                for (int i = 0; i < amount; i++) {
                    returnList.add(woodList.removeLast());
                }
                break;
            case "Stone":
                if (stoneList.size() < amount) {
                    throw new IllegalArgumentException("Not enough stone");
                }
                for (int i = 0; i < amount; i++) {
                    returnList.add(stoneList.removeLast());
                }
                break;
            case "Brick":
                if (brickList.size() < amount) {
                    throw new IllegalArgumentException("Not enough brick");
                }
                for (int i = 0; i < amount; i++) {
                    returnList.add(brickList.removeLast());
                }
                break;
            case "Wheat":
                if (wheatList.size() < amount) {
                    throw new IllegalArgumentException("Not enough wheat");
                }
                for (int i = 0; i < amount; i++) {
                    returnList.add(wheatList.removeLast());
                }
                break;
            case "Sheep":
                if (sheepList.size() < amount) {
                    throw new IllegalArgumentException("Not enough sheep");
                }
                for (int i = 0; i < amount; i++) {
                    returnList.add(sheepList.removeLast());
                }
                break;
        }
        return returnList;
    }
}
