package org.example.catan.graph;

import lombok.Getter;

/**
 * Enum representing the six neighboring directions in the axial hex coordinate system.
 * Each entry includes its displacement in the (q, r) coordinate system.
 */
@Getter
public enum Directions {
    /** Northeast direction: displacement (1, -1). */
    NORTH_EAST(1, -1),

    /** East direction: displacement (1, 0). */
    EAST(1, 0),

    /** Southeast direction: displacement (0, 1). */
    SOUTH_EAST(0, 1),

    /** Southwest direction: displacement (-1, 1). */
    SOUTH_WEST(-1, 1),

    /** West direction: displacement (-1, 0). */
    WEST(-1, 0),

    /** Northwest direction: displacement (0, -1). */
    NORTH_WEST(0, -1);

    /** Change in the q-axis (axial coordinate system). */
    private final int dq;

    /** Change in the r-axis (axial coordinate system). */
    private final int dr;

    /**
     * Constructor for a direction with the given axial displacements.
     *
     * @param dq Displacement along the q-axis
     * @param dr Displacement along the r-axis
     */
    Directions(int dq, int dr) {
        this.dq = dq;
        this.dr = dr;
    }
}
