package student;

import game.EscapeState;
import game.ExplorationState;
import game.NodeStatus;
import game.Node;
import game.Tile;



import java.util.*;

public class Explorer {

  // the list of all blocks visited
  private ArrayList<long[]> breadcrumTrail = new ArrayList();

  // a breadcrum for every block
  private long[] breadcrum = new long[2];



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
    while(state.getDistanceToTarget() > 0){
      state.moveTo(closestNeighbourToOrb(state.getNeighbours(), state));
      breadcrumSetter(state.getCurrentLocation());
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
  }

  /**
   *
   * @param neighbours  Receives a collection of all available neighbours
   * @param state       Receives the current state
   * @return            returns the nearest location to the orb excluding the current location)
   */
  public long closestNeighbourToOrb(Collection<NodeStatus> neighbours, ExplorationState state){
    long result = neighbours.iterator().next().getId();
    for (NodeStatus neighbour : neighbours){
      System.out.println("the current neighbour is:" + neighbour.getId() + " and it's link is: " + neighbour + "and it was visited:" + breadcrumCounter(neighbour.getId()));
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


}
