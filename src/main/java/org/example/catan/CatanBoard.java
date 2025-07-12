package org.example.catan;

import lombok.Getter;
import org.example.catan.gamepieces.Resources;
import org.example.catan.graph.Directions;
import org.example.catan.graph.HexTile;
import org.example.catan.graph.IntTupel;
import org.example.catan.graph.Node;

import java.util.*;

/**
 * Represents the game board for Settlers of Catan.
 * Manages the hex tiles, nodes (vertices), and streets (edges) that form the playable area.
 */
@Getter
public class CatanBoard {
    private static final int STREET = 0;
    private static final int PLAYER = 1;
    static Node[] nodes;
    IntTupel[] hex_coords;
    Map<IntTupel, HexTile> board = new HashMap<>();
    int[][][] graph;

    /**
     * Constructs a new CatanBoard with the given radius.
     * Initializes the nodes, graph, hex tile coordinates, and the full board graph.
     *
     * @param radius Number of hex rings from the center outward.
     */
    public CatanBoard(int radius) {
        initNodes(radius);
        initGraph();
        initHexCoords(radius);
        createGraph();
    }

    /**
     * Recursively calculates the number of nodes required for a hexagonal grid of radius n.
     *
     * @param n The radius of the board.
     * @return The total number of nodes.
     */
    private static int calcNumNodes(int n) {
        if (n <= 0) return 0;
        return calcNumNodes(n - 1) + (2 * n - 1) * 6;
    }

    /**
     * Initializes the static array of nodes.
     *
     * @param n The radius of the board.
     */
    public static void initNodes(int n) {
        int numNodes = calcNumNodes(n);
//        System.out.println("Number of nodes: " + numNodes);
        nodes = new Node[numNodes];
        for (int i = 0; i < numNodes; i++) {
            nodes[i] = new Node(i);
        }
    }

    /**
     * Recursively calculates the number of hex tiles needed for a given radius.
     *
     * @param n The radius of the board.
     * @return The number of hex tiles.
     */
    private static int calcNumHexTiles(int n) {
        if (n == 1) return 1;
        return calcNumHexTiles(n - 1) + 6 * (n - 1);
    }

    /**
     * Generates the resource types for the hex tiles excluding the desert tile.
     *
     * @param numTiles Number of resource-producing tiles.
     * @return List of resource types.
     */
    private static ArrayList<Resources> generateResourceTypes(int numTiles) {
        ArrayList<Resources> allResources = new ArrayList<>();
        Resources[] values = Resources.values();

        for (int i = 0; i < numTiles; i++) {
            if (values[i % values.length] != Resources.NONE) {
                allResources.add(values[i % values.length]);
            } else {
                numTiles++; // Skip desert
            }
        }
        return allResources;
    }

    /**
     * Returns a shuffled list of dice numbers used for hex tiles.
     *
     * @return A shuffled list of dice numbers (excluding desert).
     */
    private static ArrayList<Integer> generateDiceNumbers() {
        ArrayList<Integer> diceNumbers = new ArrayList<>(Arrays.asList(
                2, 3, 3, 4, 4, 5, 5, 6, 6,
                8, 8, 9, 9, 10, 10, 11, 11, 12
        ));

        Collections.shuffle(diceNumbers);
        return diceNumbers;
    }

    /**
     * Initializes the street graph with no existing roads and no ownership.
     */
    private void initGraph() {
        graph = new int[nodes.length][nodes.length][2];
        for (int i = 0; i < nodes.length; i++) {
            for (int j = 0; j < nodes.length; j++) {
                graph[i][j][STREET] = 0;
                graph[i][j][PLAYER] = -1;
            }
        }
    }

    /**
     * Updates the street connection between two nodes and assigns it to a player.
     *
     * @param i        Node ID 1
     * @param j        Node ID 2
     * @param existenz 1 if road exists, 0 otherwise
     * @param spieler  ID of owning player, -1 if none
     */
    public void updateGraph(int i, int j, int existenz, int spieler) {
        graph[i][j][STREET] = existenz;
        graph[i][j][PLAYER] = spieler;
        graph[j][i][STREET] = existenz;
        graph[j][i][PLAYER] = spieler;
    }

    /**
     * Initializes axial coordinates for all hex tiles based on the board radius.
     *
     * @param radius Radius of the board.
     */
    private void initHexCoords(int radius) {
        hex_coords = new IntTupel[calcNumHexTiles(radius)];
        int index = 0;
        for (int i = -radius + 1; i < radius; i++) {
            for (int j = -radius + 1; j < radius; j++) {
                if (Math.abs(i + j) < radius) {
                    hex_coords[index] = new IntTupel(j, i);
                    index++;
                }
            }
        }
    }

    /**
     * Creates all hex tiles and connects their corner nodes in the graph.
     * Randomly assigns resources and dice numbers.
     *
     */
    private void createGraph() {
        int index = 0;
        Directions[] DIR = {
                Directions.NORTH_WEST,
                Directions.NORTH_EAST,
                Directions.WEST
        };

        ArrayList<Resources> allResources = generateResourceTypes(hex_coords.length - 1);
        ArrayList<Integer> allDiceNumbers = generateDiceNumbers();
        allResources.add(Resources.NONE); // Add desert

        Random rand = new Random();

        for (IntTupel coords : hex_coords) {
            Node[] HexNodes = new Node[6];

            for (Directions dir : DIR) {
                IntTupel neighbor = new IntTupel(coords.q() + dir.getDq(), coords.r() + dir.getDr());
                if (board.containsKey(neighbor)) {
                    switch (dir) {
                        case NORTH_WEST:
                            HexNodes[5] = board.get(neighbor).getHexTileNodes()[3];
                            HexNodes[0] = board.get(neighbor).getHexTileNodes()[2];
                            break;
                        case NORTH_EAST:
                            HexNodes[0] = board.get(neighbor).getHexTileNodes()[4];
                            HexNodes[1] = board.get(neighbor).getHexTileNodes()[3];
                            break;
                        case WEST:
                            HexNodes[4] = board.get(neighbor).getHexTileNodes()[2];
                            HexNodes[5] = board.get(neighbor).getHexTileNodes()[1];
                            break;
                    }
                }
            }

            for (int i = 0; i < HexNodes.length; i++) {
                if (HexNodes[i] == null) {
                    HexNodes[i] = nodes[index];
                    int nextIndex = (i + 1 + HexNodes.length) % HexNodes.length;
                    if (HexNodes[nextIndex] != null) {
                        updateGraph(index, HexNodes[nextIndex].id, 1, -1);
                    }
                    int prevIndex = (i - 1 + HexNodes.length) % HexNodes.length;
                    if (HexNodes[prevIndex] != null) {
                        updateGraph(index, HexNodes[prevIndex].id, 1, -1);
                    }
                    index++;
                }
            }

            int randomIndex = rand.nextInt(allResources.size());
            Resources selectedResource = allResources.get(randomIndex);
            int diceNumber = selectedResource.equals(Resources.NONE) ? 0 : allDiceNumbers.removeFirst();

            board.put(coords, new HexTile(diceNumber, selectedResource, HexNodes));
            allResources.remove(randomIndex);
        }

    }

}
