package student;

import game.EscapeState;
import game.ExplorationState;
import game.Node;
import game.NodeStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Explorer {
    ToolKit toolkit = new ToolKit();

    /**
     * Explore the cavern, trying to find the orb in as few steps as possible.
     * Once you find the orb, you must return from the function in order to pick
     * it up. If you continue to move after finding the orb rather
     * than returning, it will not count.
     * If you return from this function while not standing on top of the orb,
     * it will count as a failure.
     * <p>
     * There is no limit to how many steps you can take, but you will receive
     * a score bonus multiplier for finding the orb in fewer steps.
     * <p>
     * At every step, you only know your current tile's ID and the ID of all
     * open neighbor tiles, as well as the distance to the orb at each of these tiles
     * (ignoring walls and obstacles).
     * <p>
     * To get information about the current state, use functions
     * getCurrentLocation(),
     * getNeighbours(), and
     * getDistanceToTarget()
     * in ExplorationState.
     * You know you are standing on the orb when getDistanceToTarget() is 0.
     * <p>
     * Use function moveTo(long id) in ExplorationState to move to a neighboring
     * tile by its ID. Doing this will change state to reflect your new position.
     * <p>
     * A suggested first implementation that will always find the orb, but likely won't
     * receive a large bonus multiplier, is a depth-first search.
     *
     * @param state the information available at the current state
     */
    public void explore(ExplorationState state) {
        while (state.getDistanceToTarget() > 0) {
            Collection<NodeStatus> neighbours = state.getNeighbours();
            state.moveTo(toolkit.closestNeighbourToOrb(neighbours, state));
            toolkit.breadcrumSetter(state.getCurrentLocation());
        }
    }

    /**
     * Escape from the cavern before the ceiling collapses, trying to collect as much
     * gold as possible along the way. Your solution must ALWAYS escape before time runs
     * out, and this should be prioritized above collecting gold.
     * <p>
     * You now have access to the entire underlying graph, which can be accessed through EscapeState.
     * getCurrentNode() and getExit() will return you Node objects of interest, and getVertices()
     * will return a collection of all nodes on the graph.
     * <p>
     * Note that time is measured entirely in the number of steps taken, and for each step
     * the time remaining is decremented by the weight of the edge taken. You can use
     * getTimeRemaining() to get the time still remaining, pickUpGold() to pick up any gold
     * on your current tile (this will fail if no such gold exists), and moveTo() to move
     * to a destination node adjacent to your current node.
     * <p>
     * You must return from this function while standing at the exit. Failing to do so before time
     * runs out or returning from the wrong location will be considered a failed run.
     * <p>
     * You will always have enough time to escape using the shortest path from the starting
     * position to the exit, although this will not collect much gold.
     *
     * @param state the information available at the current state
     */
    public void escape(EscapeState state) {
        /**
         * Knowing the first tile (using id),
         * it is possible to find all of it's neighbours. Since all the neighbours are adjusent to the
         * initial tile it is possible to reach those in one step. The collection of nodes reachable in one step are
         *  the first layer of possible steps. Proceeding from there, it is possible to find the  neighbours of  all tiles
         * in  the  first layer, ignoring duplicates, as some tiles may share a neighbour. This will be the second layer.
         * Then find the third layer and so on. The final
         * result will be a series of layers expanding from the initial tile, though all possible paths
         * until the destination is found. Even though there may be numerous possible paths from beginning
         * to destination, there is a single shortest path from destination to beginning. As long as the
         * explorer steps on tiles which are in a layer smaller than the current one.
         */
        ArrayList<Node> finalPath;
        toolkit.clearBreadcrumTrail();
        Boolean enoughtime = true;

        if (state.getCurrentNode().getTile().getGold() > 0) {
            state.pickUpGold();
        }

        // Search for gold. I still have time!
        while (enoughtime) {
            Node exit = state.getExit();
            Node currentNode = state.getCurrentNode();
            Node closestTileWithGold = toolkit.nextClosestNodeWithGold(currentNode, toolkit.sortAllNodesByGold(state.getVertices()));
            int time = state.getTimeRemaining();
            enoughtime = toolkit.enoughTimeToKeepSearching(time,currentNode,closestTileWithGold,exit);

            Node closestGold = toolkit.nextClosestNodeWithGold(state.getCurrentNode(), toolkit.sortAllNodesByGold(state.getVertices()));
            finalPath = toolkit.getShortestPathToNode(state.getCurrentNode(), closestGold);

            Iterator<Node> finalPathIter = finalPath.iterator();
            while(finalPathIter.hasNext() && enoughtime){
                state.moveTo(finalPathIter.next());
                if (state.getCurrentNode().getTile().getGold() > 0) {
                    state.pickUpGold();
                }
            }
        }

        // Time is running out! Run to the exit! Touch nothing!
        finalPath = toolkit.getShortestPathToNode(state.getCurrentNode(), state.getExit());
        for (Node n : finalPath) {
            state.moveTo(n);
        }

    }

}
