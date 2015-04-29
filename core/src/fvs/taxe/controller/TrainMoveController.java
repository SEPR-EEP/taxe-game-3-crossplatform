package fvs.taxe.controller;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import fvs.taxe.actor.TrainActor;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.Player;
import gameLogic.TurnListener;
import gameLogic.map.CollisionStation;
import gameLogic.map.IPositionable;
import gameLogic.map.Position;
import gameLogic.map.Station;
import gameLogic.resource.Resource;
import gameLogic.resource.Train;

import java.util.ArrayList;
import java.util.List;

import Util.InterruptableSequenceAction;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;

/**Controller for moving trains.*/
public class TrainMoveController {
	/**The chance (as a decimal) of a junction failing.*/
	private static final float JUNCTION_FAILURE_CHANCE = 0.2f;
	
	/**The context of the game.*/
	private Context context;
	
	/**The train being controlled by the controller.*/
	private Train train;
	
	/**The action being applied to the train currently being controlled.*/
	private InterruptableSequenceAction action;

	/**Instantiation adds a turn listener to interrupt the train's action when a turn changes.
	 * @param context The game context.
	 * @param train The train to be controlled.
	 */
	public TrainMoveController(final Context context, final Train train) {
		this.context = context;
		this.train = train;

		context.getGameLogic().getPlayerManager().subscribeTurnChanged(new TurnListener() {
			// only set back the interrupt so the train can move after the turn has changed (players turn ended)
			@Override
			public void changed() {
				action.setInterrupt(false);
			}
		});
		
		addMoveActions();
	}

	/**This method produces an action for the train to run before moving on the screen.
	 * @return An action where the train is set to visible and off the screen.
	 */
	private RunnableAction beforeAction() {
		return new RunnableAction() {
			public void run() {
				train.getActor().setVisible(true);
				train.setPosition(new Position(-1, -1));
			}
		};
	}

	/**This method produces an action to run every time a train reaches a station on it's route.
	 * @param stationReached The station reached.
	 * @return An action which adds the train movement to the move history and continues the journey of the train.
	 */
	private RunnableAction perStationAction(final Station stationReached, final Station nextStationOfRoute) {
		return new RunnableAction() {
			public void run() {
				train.addHistory(stationReached.getName(), context.getGameLogic().getPlayerManager().getTurnNumber());
				System.out.println("Added to history: passed " + stationReached.getName() + " on turn "
						+ context.getGameLogic().getPlayerManager().getTurnNumber());
				
				junctionFailure(stationReached);
				collisions(stationReached);
				obstacleCollision(stationReached);

				train.setNextStationOfRoute(nextStationOfRoute);
			}

		};
	}

	/**This method checks whether a train has failed upon reaching a statement using the junction failiure chance. If it has, the movement is interrupted.*/
	private void junctionFailure(Station station) {
		// calculate if a junction failure has occured- if it has, stop the train at the station for that turn
		if (station instanceof CollisionStation){
			boolean junctionFailed = MathUtils.randomBoolean(JUNCTION_FAILURE_CHANCE);
			if (Game.getInstance().replayMode == false && junctionFailed && station != train.getRoute().get(0)) {
				action.setInterrupt(true);
				context.getTopBarController().displayObstacleMessage("Junction failed, " + train.getName() + " stopped!", Color.YELLOW);
			}
		}
	}

	/**This method produces an action for when the train has reached it's final destination.
	 * @return A runnable action that displays a message and notifies the goal manager.
	 */
	private RunnableAction afterAction() {
		return new RunnableAction() {
			public void run() {
				ArrayList<String> completedGoals = context.getGameLogic().getGoalManager().trainArrived(train, train.getPlayer());
				for(String message : completedGoals) {
					context.getTopBarController().displayFlashMessage(message, Color.WHITE, 2);
				}
				System.out.println(train.getFinalDestination().getLocation().getX() + "," + train.getFinalDestination().getLocation().getY());
				train.setPosition(train.getFinalDestination().getLocation());
				train.getActor().setVisible(false);
				train.setFinalDestination(null);
			}
		};
	}

	/**This method uses the current's train's routes to create a set of move actions for the train.*/
	public void addMoveActions() {
		action = new InterruptableSequenceAction();

		Station firstStation = train.getRoute().get(0);
		Station secondStation = train.getRoute().get(1);
		boolean rerouting = firstStation == secondStation;
		IPositionable current;
		if ( rerouting ) {
			current = new Position((int)(train.getActor().getX()+train.getActor().width/2),         
		         				(int)(train.getActor().getY()+train.getActor().height/2));                                    
		} else {
			current = train.getPosition();
		}
//		System.out.println("Y = " + train.getActor().getY());
//		System.out.println("Height = " + train.getActor().height/2);


		System.out.println("Initial position of the train atm is: " + current.getX() + ", " + current.getY());

		action.addAction(beforeAction());
		
		if (!rerouting) {
			action.addAction(moveTo(firstStation.getLocation().getX() - TrainActor.width / 2, firstStation.getLocation().getY() - TrainActor.height / 2, 0));
		}
		
		boolean first = true;
		int stationIndex = 0;

		for (final Station station : train.getRoute()) {

			// We want to skip the first iteration (from nowhere to intial station!)
			if ( first ) {
				first = false;
				continue;
			}
			
			IPositionable next = station.getLocation();
			
			System.out.println("Train Speed = " + train.getSpeed());
			System.out.println("Train Position = " + current.getX() + " " + current.getY());
			System.out.println("Next Position = " + next.getX() + " " + next.getY());System.out.println("Train Speed = " + train.getSpeed());
			System.out.println("Distance = " + getDistance(current, next));
			
			float duration = getDistance(current, next) / train.getSpeed();
			System.out.println("I will move the train for " + duration + " seconds \n");
			action.addAction(moveTo(next.getX() - TrainActor.width / 2, next.getY() - TrainActor.height / 2, duration));
			
			Station nextStationOfRoute;
			if (stationIndex < train.getRoute().size() - 1){
				nextStationOfRoute = train.getRoute().get(stationIndex+1);
			} else {
				nextStationOfRoute = null;
			}
			//Team EEP has changed code to also pass the next station of the route
			action.addAction(perStationAction(station, nextStationOfRoute));
			current = next;
			stationIndex++;
		}

		action.addAction(afterAction());

		// remove previous actions to be cautious
		train.getActor().clearActions();
		train.getActor().addAction(action);
	}

	/**
	 * @param a A position.
	 * @param b A second position.
	 * @return The distance between the 2 positions.
	 */
	private float getDistance(IPositionable a, IPositionable b) {
		return Vector2.dst(a.getX(), a.getY(), b.getX(), b.getY());
	}

	/**This method tests for collisions when a train reaches a junction. If there is a collision, both trains are destroyed.
	 * @param station The station to test.
	 */
	private void collisions(Station station) {
		//test for train collisions at Junction point
		if(Game.getInstance().replayMode || !(station instanceof CollisionStation)) {
			return;
		}
		List<Train> trainsToDestroy = collidedTrains();

		if(trainsToDestroy.size() > 0) {
			for(Train trainToDestroy : trainsToDestroy) {
				if ( trainToDestroy.getActor() != null ) {
					trainToDestroy.getActor().remove();
				}
				trainToDestroy.getPlayer().removeResource(trainToDestroy);
			}

			context.getTopBarController().displayFlashMessage("Two trains collided at a Junction.  They were both destroyed.", Color.BLACK, Color.RED, 4);
		}
	}

	/**This method checks if the train has collided with an obstacle when it reaches a station. If it has, the train is destroyed.*/
	private void obstacleCollision(Station station) {
		// works out if the station has an obstacle active there, whether to destroy the train
		if (Game.getInstance().replayMode == false && station.hasObstacle() && MathUtils.randomBoolean(station.getObstacle().getDestructionChance())){

			Game.getInstance().setTrainToDestroy(train);
			Game.getInstance().setState(GameState.DESTROYING);
		}
	}

	/**This method returns the list of trains that the train has collided with at a junction.
	 * @return A list of trains that the current train collided with.
	 */
	private List<Train> collidedTrains() {
		List<Train> trainsToDestroy = new ArrayList<Train>();

		for(Player player : context.getGameLogic().getPlayerManager().getAllPlayers()) {
			for(Resource resource : player.getResources()) {
				if(resource instanceof Train) {
					Train otherTrain = (Train) resource;
					if(otherTrain.getActor() == null) continue;
					if(otherTrain == train) continue;

					if(train.getActor().getBounds().overlaps(otherTrain.getActor().getBounds())) {
						//destroy trains that have crashed and burned
						trainsToDestroy.add(train);
						trainsToDestroy.add(otherTrain);
					}
				}
			}
		}

		return trainsToDestroy;
	}
}
