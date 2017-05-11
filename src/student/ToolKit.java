package student;

import game.ExplorationState;
import game.Node;
import game.NodeStatus;

import java.util.*;

/**
 * Created by paul on 10/5/2017.
 */
public class ToolKit {

    private ArrayList<long[]> breadcrumTrail = new ArrayList();

    private ArrayList<Set<Node>> stepMap = new ArrayList<>();
    private ArrayList<Node> finalPath = new ArrayList<>();
    private Set<Node> scanned = new HashSet<>();
    /**
     *
     * @param neighbours  Receives a collection of all available neighbours
     * @param state       Receives the current state
     * @return            returns the nearest location to the orb excluding the current location)
     */
    public long closestNeighbourToOrb(Collection<NodeStatus> neighbours, ExplorationState state){
        long result = neighbours.iterator().next().getId();
        for (NodeStatus neighbour : neighbours){
            if (neighbour.getDistanceToTarget() < state.getDistanceToTarget() && breadcrumCounter(neighbour.getId())==0){
                result = neighbour.getId();
            }else if(breadcrumCounter(result) > breadcrumCounter(neighbour.getId())){
                result = neighbour.getId();
            }
        }
        return result;
    }

    public void breadcrumSetter(long id){
        if (breadcrumGetter(id)[0] == 0){
            long[] add = {id, 1};
            breadcrumTrail.add(add);
        }else{
            breadcrumGetter(id)[1]++;
        }
    }

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

    public Set<Node> getUniqueNeighbours(Node node){
        Set<Node> uniqueNodeCollection = new HashSet<>();

        for (Node n : node.getNeighbours()){
            if (!scanned.contains(n)) {
                uniqueNodeCollection.add(n);
            }
        }
        return uniqueNodeCollection;
    }


    public Set<Node> getLastStep(){
        Set<Node> result = stepMap.get(stepMap.size() - 1);
        return result;
    }

    public ArrayList<Node> getShortestPathToExit(Node start, Node exit){
        Set<Node> potentialSteps = new HashSet<>();
        Set<Node> lastEntry = new HashSet<>();

        potentialSteps.add(start);
        stepMap.add(potentialSteps);
        scanned.addAll(potentialSteps);

        do {
            potentialSteps = new HashSet<>();
            lastEntry = getLastStep();
            for (Node n : lastEntry) {
                potentialSteps.addAll(getUniqueNeighbours(n));
                scanned.addAll(getUniqueNeighbours(n));
            }
            stepMap.add(potentialSteps);
        }while(!scanned.contains(exit));



        Collections.reverse(stepMap);
        Node nextStep = exit;
        finalPath.add(nextStep);

        for (int i = 1; i < stepMap.size()-1; i++){
            for(Node n : stepMap.get(i)){
                if (nextStep.getNeighbours().contains(n)){
                    nextStep = n;
                    finalPath.add(n);
                }
            }
        }

        Collections.reverse(finalPath);
        return finalPath;
    }

    public int timeLeftPercentage (int startTime, int currentTime){
        int result = (currentTime * 100)/startTime;
        return result;
    }

    public Node randomNode(Set<Node> pickOne){
        /**
        Random randomGenerator = new Random();
        ArrayList<Node> nodePool = new ArrayList<>();
        nodePool.addAll(pickOne);
        int index = randomGenerator.nextInt(nodePool.size());
        return nodePool.get(index);
         */

        Node result = pickOne.iterator().next();
        for(Node n : pickOne){
            if (breadcrumCounter(n.getId()) <= breadcrumCounter(result.getId())) {
                result = n;
            }
        }
        return result;
    }

    public Node getTileWithMostGold(Set<Node> pickOne){
        Node result = pickOne.iterator().next();
        for(Node n : pickOne){
            if (n.getTile().getGold() > result.getTile().getGold()){
                result = n;
            }
        }
        return result;
    }

    public void clearBreadcrumTrail(){
        breadcrumTrail.clear();
    }

}
