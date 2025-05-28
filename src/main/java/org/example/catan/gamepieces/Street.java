package org.example.catan.gamepieces;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.catan.Player;

@EqualsAndHashCode(callSuper = true)
@Data
public class Street extends Building {
    public String edge;
    public Street( String edge, Player owner) {
        this.name = "Street";
        this.edge = edge;
        this.owner = owner;
    }
}
