package org.example.catan.gamepieces;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.catan.Player;

@EqualsAndHashCode(callSuper = true)
@Data
public class Settlement extends Building {
    public String vertex;
    public Settlement(String vertex, Player owner) {
        this.name = "Settlement";
        this.vertex = vertex;
        this.owner = owner;
    }
}
