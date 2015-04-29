package gameLogic.goal;

import gameLogic.Game;
import gameLogic.Player;
import gameLogic.map.CollisionStation;
import gameLogic.map.Map;
import gameLogic.map.Station;
import gameLogic.resource.ResourceManager;
import gameLogic.resource.Train;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Util.Node;
import Util.Tuple;
import java.io.Serializable;

/**This class manages goals for the game.*/
public class GoalManager implements Serializable {
	/**The maximum number of goals a player can have.*/
	public final static int CONFIG_MAX_PLAYER_GOALS = 3;
	
	/**The ResourceManager for the Game.*/
	private ResourceManager resourceManager;
	
	/**The List of listeners that are notified when a goal is finished.*/
	private static List<GoalListener> listeners;
	
	/**Instantiation method.
	 * @param resourceManager The ResourceManager for the Game.
	 */
	public GoalManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
		listeners = new ArrayList<GoalListener>();
	}

	/**This method generates a random goal with extra constraints. It chooses a random origin and destination, and adds extra constraints to it.
	 * @param turn The turn in which the goal was created.
	 * @param extraConstraints The number of extra constraints to add. Can be zero.
	 * @return
	 */
	public Goal generateRandomGoal(int turn, int extraConstraints) {

        Map map = Game.getInstance().getMap();
        Station origin, destination;
        List<Station> idealRoute;

        //We need to create a goal with two distinct stations that has a valid routing


        do {
            origin = map.getRandomStation();
            destination = map.getRandomStation();
            idealRoute = generateGoalRoute(map, origin, destination);
        } while ( origin instanceof CollisionStation || destination == origin  || destination instanceof CollisionStation || idealRoute == null );


		Goal goal = new Goal(origin, destination, turn, idealRoute);
				
		//Check if we need to complicate the Goal with further constraints
		if(extraConstraints > 0)
		{
			//Generate a set of constraints to add to the goal
			ArrayList<Tuple<String, Object>> availableConstraints = generateExtraConstraints(idealRoute, map.getRouteLength(idealRoute));
			for(int i = 0; i < extraConstraints; i++)
			{
				//Pick one of our available constraints and add it to the goal
				Tuple<String, Object> goalConstraint = availableConstraints.get(new Random().nextInt(availableConstraints.size()));
				availableConstraints.remove(goalConstraint);
				
				goal.addConstraint(resourceManager, goalConstraint.getFirst(), goalConstraint.getSecond());
			}
		}
		return goal;
	}

    private List<Station> generateGoalRoute(Map map, Station origin, Station destination){
        //Find the ideal solution to solving this objective
        Node<Station> originNode = new Node<Station>();
        originNode.setData(origin);
        ArrayList<Node<Station>> searchFringe = new ArrayList<Node<Station>>();
        searchFringe.add(originNode);
        List<Station> idealRoute = map.getIdealRoute(destination, searchFringe, map.getStationsList());
        return idealRoute;
    }
	
	/**This method generates a set of extra constraints using the ideal Route.
	 * @param idealRoute The ideal route for which the constraints should be generated.
	 * @param routeLength The length of the ideal Route.
	 * @return The List of Constraints generated.
	 */
	private ArrayList<Tuple<String, Object>> generateExtraConstraints(List<Station> idealRoute, float routeLength) {
		ArrayList<Tuple<String, Object>> list =  new ArrayList<Tuple<String, Object>>();
		//Add a constraint based on number of turns, based on the time taken for a Bullet Train to complete the route of Param routeLength
		list.add(new Tuple<String, Object>("turnCount", (int)Math.ceil((routeLength / resourceManager.getTrainSpeed("Bullet Train")))));
		//Add a constraint based on the number of trains completing the same goal, with a random value of either 2 or 3
		list.add(new Tuple<String, Object>("trainCount", new Random().nextInt(2) + 2));
		//Add a constraint based on the train type, picking a random train type
		list.add(new Tuple<String, Object>("trainType", resourceManager.getTrainNames().get(new Random().nextInt(resourceManager.getTrainNames().size()))));
		//If the route is not linear between 2 points, then we can add an exclusion constraint from the idealRoute
		List<Station> removeAbleStations = Game.getInstance().getMap().getEditableRoute(idealRoute);
		if(removeAbleStations.size() > 0)
		{
			if(removeAbleStations.size() == 1)
			{
				list.add(new Tuple<String, Object>("exclusionStation", removeAbleStations.get(0)));
				
			}
			else
			{
				list.add(new Tuple<String, Object>("exclusionStation", removeAbleStations.get(new Random().nextInt(removeAbleStations.size()))));
			}
		}
		//Add a constraint of the maximum number of journeys a train can make to get between the 2 locations, the length of the ideal route + 1 (since the ideal route contains the origin)
		list.add(new Tuple<String, Object>("locationCount", idealRoute.size()));
		return list;
	}
	
	/**This method is called when the turn changes. The current player is updated.
	 * @param player The player to updated.
	 */
	public void updatePlayerGoals(Player player)
	{
		player.updateGoals(this);
	}

	/**This method is called when a train reaches a station. The current player's goals are checked for completion on this event.
	 * @param train The train that has triggered the event.
	 * @param player The current player.
	 * @return A list of Strings to display if the player completed any goals.
	 */
	public ArrayList<String> trainArrived(Train train, Player player) {
		ArrayList<String> completedString = new ArrayList<String>();
		for(Goal goal:player.getGoals()) {
			//Check if a goal was completed by the train arrival
			if(goal.isComplete(train)) {
				player.completeGoal(goal);
				player.removeResource(train);
				completedString.add("Player " + player.getPlayerNumber() + " completed a goal to " + goal.toString() + "!");
				goalFinished(goal);
			}
		}
		System.out.println("Train arrived to final destination: " + train.getFinalDestination().getName());
		return completedString;
	}

	/** Adds a new GoalListener that is notified when a goal is finished.*/
	public void subscribeGoalFinished(GoalListener goalFinishedListener) {
		listeners.add(goalFinishedListener);
	}
	
	/**This method is called when a goal is finished. All of the GoalListeners are notified.*/
	public void goalFinished(Goal goal) {
		for (GoalListener listener : listeners){
			listener.finished(goal);
		}
	}
}
