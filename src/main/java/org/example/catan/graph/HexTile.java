package org.example.catan.graph;

import lombok.Setter;
import lombok.ToString;
import lombok.Getter;
import org.example.catan.gamepieces.Resources;

/**
 * Represents a hexagonal tile on the board.
 * Each tile has a dice number, a resource type, six corner nodes, and a blocked status.
 */
@Getter
@ToString
public class HexTile {

    /** Dice number for this tile (used to produce resources). */
    private final int diceNumber;

    /** The type of resource this tile produces. */
    private final Resources resourceType;

    /** The six corner nodes of this tile. */
    private final Node[] HexTileNodes;

    /** Indicates whether the tile is blocked (e.g. by the robber). */
    @Setter
    private boolean blocked;

    /**
     * Creates a new hex tile with given dice number, resource type and corner nodes.
     *
     * @param diceNumber the dice number assigned to this tile
     * @param resourceType the resource type of this tile
     * @param nodes the six corner nodes of the tile
     */
    public HexTile(int diceNumber, Resources resourceType, Node[] nodes) {
        this.diceNumber = diceNumber;
        this.resourceType = resourceType;
        this.HexTileNodes = nodes;
        this.blocked = false;
    }
}
