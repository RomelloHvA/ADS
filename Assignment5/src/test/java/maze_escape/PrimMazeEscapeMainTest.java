package maze_escape;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PrimMazeEscapeMainTest {

    private static final long SEED = 20231113L;
    private static final int WIDTH = 100;
    private static final int HEIGHT = WIDTH;
    private static final int REMOVE = 250;

    private Maze maze = new Maze(WIDTH, HEIGHT);

    @BeforeEach
    void setUp() {
        Maze.reSeedRandomizer(SEED); // Use the same seed for reproducibility
        maze.generateRandomizedPrim();
        maze.configureInnerEntry();
        maze.removeRandomWalls(REMOVE);
    }

    @Test
    void testMazeGeneration() {
        assertEquals(WIDTH, maze.getWidth());
        assertEquals(HEIGHT, maze.getHeight());
        assertTrue(maze.getNumberOfCells() > 0);
    }

    @Test
    void testPathFinding() {
        var pathDFS = maze.depthFirstSearch(maze.getStartNode(), maze.getExitNode());
        var pathBFS = maze.breadthFirstSearch(maze.getStartNode(), maze.getExitNode());
        var pathDijkstra = maze.dijkstraShortestPath(maze.getStartNode(), maze.getExitNode(), maze::manhattanTime);

        assertNotNull(pathDFS);
        assertNotNull(pathBFS);
        assertNotNull(pathDijkstra);

        if (pathDFS.getTotalWeight() == 0.0) pathDFS.reCalculateTotalWeight(maze::manhattanTime);
        if (pathBFS.getTotalWeight() == 0.0) pathBFS.reCalculateTotalWeight(maze::manhattanTime);

        System.out.println("DFS: " + pathDFS);
        System.out.println("BFS: " + pathBFS);
        System.out.println("Dijkstra: " + pathDijkstra);

        //weight
        assertTrue(pathDFS.getTotalWeight() > pathBFS.getTotalWeight());
        assertTrue(pathDFS.getTotalWeight() > pathDijkstra.getTotalWeight());
        assertEquals(pathDijkstra.getTotalWeight(), pathBFS.getTotalWeight());

        //length
        assertTrue(pathDFS.getVertices().size() > pathBFS.getVertices().size());
        assertTrue(pathDFS.getVertices().size() > pathDijkstra.getVertices().size());
        assertEquals(pathDijkstra.getVertices().size(), pathBFS.getVertices().size());

        //exit
        assertTrue(pathDFS.getVertices().contains(maze.getExitNode()));
        assertTrue(pathBFS.getVertices().contains(maze.getExitNode()));
        assertTrue(pathDijkstra.getVertices().contains(maze.getExitNode()));

        //entry
        assertTrue(pathDFS.getVertices().contains(maze.getStartNode()));
        assertTrue(pathBFS.getVertices().contains(maze.getStartNode()));
        assertTrue(pathDijkstra.getVertices().contains(maze.getStartNode()));

        //visited
        assertTrue(pathDFS.getVisited().size() > pathBFS.getVisited().size());
        assertTrue(pathDFS.getVisited().size() < pathDijkstra.getVisited().size());
        assertTrue(pathDijkstra.getVisited().size() > pathBFS.getVisited().size());


    }


}
