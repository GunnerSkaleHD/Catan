package org.example.catan.graph;

import lombok.Getter;
import lombok.ToString;

/**
 * Represents a corner (node) on the Catan board.
 * A node is a potential location for a settlement or city.
 * Each node has a unique ID.
 */
@Getter
@ToString
public class Node {
    public final int id;

    /**
     * Creates a new node with the given unique ID.
     *
     * @param id the unique identifier for this node
     */
    public Node(int id) {
        this.id = id;
    }
}
