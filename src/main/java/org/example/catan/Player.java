package org.example.catan;

import lombok.Data;
import org.example.catan.gamepieces.Building;

import java.util.List;

@Data
public abstract class Player {
    public List<Resource> currentResources;
    public List<Building> currentBuildings;
    public String color;

    public void buySettlement(){}

}
