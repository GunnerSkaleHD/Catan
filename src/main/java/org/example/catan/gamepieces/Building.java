package org.example.catan.gamepieces;

import lombok.Data;
import org.example.catan.Player;

@Data
public abstract class Building {
    public String name;
    public Player owner;
}
