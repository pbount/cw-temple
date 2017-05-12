package student;

import game.ExplorationState;
import game.Node;
import game.NodeStatus;
import java.util.*;

/**
 * Created by paul on 10/5/2017.
 */
public class ToolKit {

    /**
     * breadcrumTrail: In order for the explorer to know whether he visited the tile he is standing  on
     *   he  leaves  a  breadcrum  on  every  new  and  visited tile. He can then chose a direction
     *   containing the least breadcrums, until he reaches his goal.
     *
     * stepMap: The stepMap contains layers of possible steps, from a given location, to a destination.
     *    From the perspective of the  starting point,  there may be more  than one paths. From the
     *    perspective of the destination however, we can find the starting point by  stepping on
     *    tiles, which are in a previous layer. The size  of the  stepMap  provides  us  with
     *    the minimum number of steps as well.
     *
     * finalPath: following each step by layer, from the stepMap, we find a sequence of steps and store
     *    those in the finalPath.
     *
     * scanned: required for finding unique neighbours
     */
    private ArrayList<long[]> breadcrumTrail = new ArrayList();

    private ArrayList<Set<Node>> stepMap = new ArrayList<>();
    private ArrayList<Node> finalPath = new ArrayList<>();
    private Set<Node> scanned = new HashSet<>();
    /**
     *
     * Given the distance from the current location of the  explorer, as  well  as  how  many times the
     * explorer has visited a tile, this method returns the nearest  neighbour of the current tile,
     * closest to the orb.
     *
     * @param neighbours  Receives a collection of all available neighbours
     * @param state       Receives the current state
     * @return            returns the nearest location to the orb excluding the current location
     *                    (dependent on previous visits as well)
     */
    public long closestNeighbourToOrb(Collection<NodeStatus> neighbours, ExplorationState state){
        long result = neighbours.iterator().next().getId();
        for (NodeStatus neighbour : neighbours){
            if (neighbour.getDistanceToTarget() < state.getDistanceToTarget() && breadcrumCounter(neighbour.getId())==0 && neighbour.getId() != 0){
                result = neighbour.getId();
            }else if(breadcrumCounter(result) > breadcrumCounter(neighbour.getId()) && neighbour.getId() != 0){
                result = neighbour.getId();
            }
        }
        return result;
    }

    /**
     * Sets a breadcrum on a tile. If it already has a breadcrum, it increments the number.
     * @param id    Receives the Id of the tile.
     */
    public void breadcrumSetter(long id){
        if (breadcrumGetter(id)[0] == 0){
            long[] add = {id, 1};
            breadcrumTrail.add(add);
        }else{
            breadcrumGetter(id)[1]++;
        }
    }

    /**
     * Counts how many breadcrums are placed on a tile.
     * @param id    The Id of the tile.
     * @return      The number of breadcrums found on the tile.
     */
    public long breadcrumCounter(long id){
        long result = 0;
        if (breadcrumTrail.size() > 0) {
            for (long[] current : breadcrumTrail) {
                if (current[0] == id) {
                    return current[1];
                }
            }
        }
        return result;
    }

    /**
     * Gets the object  itself,  containing  the  tile  Id and  the number of breadcrums. (Required for
     * breadcrumSetter)
     * @param id    The Id of the tile.
     * @return      The object containing tile and breadcrum information.
     * TODO: The naming could use some improvement
     */
    public long[] breadcrumGetter(long id){
        long result[] = {0,0};
        if (breadcrumTrail.size() > 0) {
            for (long[] current : breadcrumTrail) {
                if (current[0] == id) {
                    return current;
                }
            }
        }
        return result;
    }

    /**
     * Receives a Node and finds all  its  neighbours (if any) which have  not  been  added in the next
     * layer of possible steps.
     * @param node  Receives a Node.
     * @return      Returns a collection of unique Nodes.
     */
    public Set<Node> getUniqueNeighbours(Node node){
        Set<Node> uniqueNodeCollection = new HashSet<>();

        for (Node n : node.getNeighbours()){
            if (!scanned.contains(n)) {
                uniqueNodeCollection.add(n);
            }
        }
        return uniqueNodeCollection;
    }

    /**
     * Returns   the   last    possible layer  of steps  found. (This layer contains  the exit as well).
     * Required as a starting point to transverse the  layers of steps in  reverse order and
     * build the finalPath.
     * @return      A set of Nodes containing the Exit.
     */
    public Set<Node> getLastStep(){
        Set<Node> result = stepMap.get(stepMap.size() - 1);
        return result;
    }

    /**
     *  Returns an Arraylist of nodes (the shortest possible path) from start to end.
     *
     * @param start     The start node.
     * @param exit      The end node.
     * @return          Returns an ArrayList of neighbouring nodes from the start (excluding start) to the end
     *                  (including the end).
     */
    public ArrayList<Node> getShortestPathToNode(Node start, Node exit){
        Set<Node> potentialSteps = new HashSet<>();
        Set<Node> lastEntry;
        scanned.clear();
        stepMap.clear();
        finalPath.clear();

        potentialSteps.add(start);
        stepMap.add(potentialSteps);
        scanned.addAll(potentialSteps);

        // Scans all possible steps until the destination
        do {
            potentialSteps = new HashSet<>();
            lastEntry = getLastStep();
            for (Node n : lastEntry) {
                potentialSteps.addAll(getUniqueNeighbours(n));
                scanned.addAll(getUniqueNeighbours(n));
            }
            stepMap.add(potentialSteps);
        }while(!scanned.contains(exit));


        // inverts the sequence of possible steps
        Collections.reverse(stepMap);
        Node nextStep = exit;
        finalPath.add(nextStep);

        // goes backwards following the neighbours contained in the previous layer
        // stores those steps in the final path
        for (int i = 1; i < stepMap.size()-1; i++){
            for(Node n : stepMap.get(i)){
                if (nextStep.getNeighbours().contains(n)){
                    nextStep = n;
                    finalPath.add(n);
                }
            }
        }

        // inverts the resulting final path and returns it.
        Collections.reverse(finalPath);
        return finalPath;
    }

    /**
     * Calculates the time remaining as percentage.
     * @param startTime     The time before the first step was taken.
     * @param currentTime   The current time.
     * @return              The time remaining as a percentage.
     */
    public int timeLeftPercentage (int startTime, int currentTime){
        int result = (currentTime * 100)/startTime;
        return result;
    }

    /**
     * Selects a node from a Set of Nodes (Not necessarily the first)
     * (No longer used.)
     * @param pickOne   Receives a set of nodes.
     * @return          Returns the first node grabbed by the iterator.
     */
    public Node anyNode(Set<Node> pickOne){
        Node result = pickOne.iterator().next();
        for(Node n : pickOne){
            if (breadcrumCounter(n.getId()) <= breadcrumCounter(result.getId())) {
                result = n;
            }
        }
        return result;
    }

    /**
     * Receives a Set of nodes and returns the tile containing the most gold.
     * @param pickOne   receives a Set of nodes.
     * @return          returns the tile with the most Gold.
     */
    public Node getTileWithMostGold(Set<Node> pickOne){
        Node result = pickOne.iterator().next();
        for(Node n : pickOne){
            if (n.getTile().getGold() > result.getTile().getGold()){
                result = n;
            }
        }
        return result;
    }

    /**
     * Empties all contents of the Breadcrum trail
     */

    public void clearBreadcrumTrail(){
        breadcrumTrail.clear();
    }

    /**
     *  Receives a Collection of Nodes and returns an arrayList containing the contents of the
     *  collection sorted by amount of Gold (The highest amount first)
     * @param allNodes  A collection of All Nodes
     * @return          An ArrayList containing nodes Sorted by amount of Gold.
     */
    public ArrayList<Node> sortAllNodesByGold(Collection<Node> allNodes){
        ArrayList<Node> result = new ArrayList<Node>();
        result.addAll(allNodes);

        Collections.sort(result, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return o1.getTile().getGold() - o2.getTile().getGold();
            }
        });

        Collections.reverse(result);
        return result;
    }

    /**
     * Finds the closest node in respect of a given node, which contains any amount of Gold.
     * @param currentNode       The reference node.
     * @param nodesWithGold     An ArrayList of Nodes
     * @return                  The closest node with gold to the reference node.
     */
    public Node nextClosestNodeWithGold(Node currentNode, ArrayList<Node> nodesWithGold) {
        Node result = nodesWithGold.get(0);
        int currentDistance = getShortestPathToNode(currentNode, result).size();
        int nextDistance;
        for (Node n : nodesWithGold) {
            nextDistance = getShortestPathToNode(currentNode, n).size();
            if (n.getTile().getGold() > 0 && currentDistance > nextDistance) {
                currentDistance = nextDistance;
                result = n;
            }
        }
        return result;
    }

    /**
     * Calculates the time to get to the exit, the time to get to the exit after visiting the
     * closest node with gold and compares to current time. If the explorer can indeed
     * reach the exit after visiting another node, he does so. Otherwise if the time
     * won't suffice to visit another node to pickup gold he moves to the exit.
     *
     * @param time                  the time currently remaining.
     * @param current               the current location of the explorer.
     * @param closestTileWithGold   the location of the closest node with gold.
     * @param exit                  the location of the exit.
     * @return
     */
    public boolean enoughTimeToKeepSearching(int time, Node current, Node closestTileWithGold, Node exit){
        int currentDistance = getShortestPathToNode(current, exit).size();
        int distanceToGold = getShortestPathToNode(current, closestTileWithGold).size();
        int distanceFromGoldToExit = getShortestPathToNode(closestTileWithGold, exit).size();
        int totalDistance = (distanceToGold + distanceFromGoldToExit);
        boolean result = (totalDistance * 10 < time) ? true : false;
        return result;
    }
}
