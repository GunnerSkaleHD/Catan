package org.example.catan.Graph;
//import de.dhbw.gamePieces.Building;
//import de.dhbw.player.Player;

import lombok.Getter;
import lombok.ToString;

/**
 *Eckpunkt auf dem Catan-Brett
 *Auf einem der Eckpunkte können nur Siedlungen oder Städte stehen
 */
@Getter
@ToString
public class Node {
    //private Building building;
    //private Player player;
    public final int id;

    public Node(int id)
    {
        this.id = id;
        //this.building = null;
        //this.player = null;
    }

//    public void setBuilding(/*Building building,*/ Player player)
//    {
//        //this.building = building;
//        this.player = player;
//    }
}