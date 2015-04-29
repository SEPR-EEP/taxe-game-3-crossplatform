package gameLogic;

import fvs.taxe.controller.StationController;
import gameLogic.goal.GoalManager;
import gameLogic.map.IPositionable;
import gameLogic.map.Map;
import gameLogic.map.Station;
import gameLogic.obstacle.Obstacle;
import gameLogic.obstacle.ObstacleListener;
import gameLogic.obstacle.ObstacleManager;
import gameLogic.resource.ResourceManager;
import gameLogic.resource.Train;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.SerializationUtils;

import Util.ActorsManager;
import Util.Tuple;

import com.badlogic.gdx.math.MathUtils;

/**Main Game class of the Game. Handles all of the game logic.*/
public class Game implements Serializable {
	/**The instance that the game is running in.*/
	private static Game instance;
	
	/**The game's PlayerManager that handles both of the players.*/
	private PlayerManager playerManager;
	
	/**The game's GoalManager that handles goals for the players.*/
	private GoalManager goalManager;
	
	/**The game's ResourceManager that handles resources for the players.*/
	private ResourceManager resourceManager;
	
	/**The game's ObstacleManager that handles Obstacles in the game.*/
	private ObstacleManager obstacleManager;
	
	/**The game map for this instance.*/
	private Map map;
	
	/**The game state.*/
	private GameState state;
	
	/**List of listeners that listen to changes in game state.*/
	private List<GameStateListener> gameStateListeners = new ArrayList<GameStateListener>();
	
	/**List of listeners that listen to changes in obstacles.*/
	private List<ObstacleListener> obstacleListeners = new ArrayList<ObstacleListener>();
	private Train confirmingTrain;

	public Train getTrainToDestroy() {
		return trainToDestroy;
	}

	public void setTrainToDestroy(Train trainToDestroy) {
		this.trainToDestroy = trainToDestroy;
	}

	private Train trainToDestroy;
	private List<IPositionable> confirmingPositions;

	public void setConfirmingTrain(Train confirmingTrain) {
		this.confirmingTrain = confirmingTrain;
	}

	public Train getConfirmingTrain() {
		return confirmingTrain;
	}

	public void setConfirmingPositions(List<IPositionable> confirmingPositions) {
		this.confirmingPositions = confirmingPositions;
	}

	public List<IPositionable> getConfirmingPositions() {
		return confirmingPositions;
	}

	/**
	 * The following class represents a single "snapshot", a state in time of the
	 * current Game, and it contains the list of properties that are strictly necessary
	 * for this purpose. All properties are meant to be publicly accessible. Note that
	 * the overall scope is limited to the Game superclass as it is a private subclass.
	 * A constructor is provided for simplicity, taking all of the properties.
	 * The whole Snapshot class and all of the properties -including all descendants-
	 * implement the Serializable interface. This allows for the Snapshot to be written
	 * to disk, a database, a network socket, etc.
	 */
	public static class Snapshot implements Serializable {
		public ArrayList<Player> players = new ArrayList<Player>();
		public int currentTurn;
		public int turnNumber;
		public GoalManager goalManager;
		public ResourceManager resourceManager;
		public ObstacleManager obstacleManager;
		public Map map;
		public GameState state;
		public List<ObstacleListener> obstacleListeners;
		public Train confirmingTrain;
		public List<IPositionable> confirmingPositions;
		public Station origin;
		public Station destination;
		public Train trainToDestroy;
		public Snapshot(
				PlayerManager b, GoalManager c, ResourceManager d, ObstacleManager e, Map f,
				GameState g, Train h, List<IPositionable> i, Station j, Station k, Train l
		) {
			players = b.getPlayers();
			currentTurn = b.getCurrentTurn();
			turnNumber = b.getTurnNumber();

			goalManager = c; resourceManager = d;
			obstacleManager = e; map = f; state = g;

			confirmingTrain = h; confirmingPositions = i;

			origin = j; destination = k;

			trainToDestroy = l;
		}
	}
	
	/** The list of all Snapshots in memory. */
	private List<byte[]> snapshots = new ArrayList<byte[]>();

	/**
	 * This method creates a Snapshot of the current state and pushes it to the
	 * list of snapshot.
	 */
	public void createSnapshot() {
		if ( this.replayMode ) {
			return;
		}
		Snapshot s = new Snapshot(playerManager, goalManager, resourceManager, obstacleManager, map,
				state, confirmingTrain, confirmingPositions, origin, destination, trainToDestroy);
		try {
			this.snapshots.add(SerializationUtils.serialize(s));
		} catch (org.apache.commons.lang3.SerializationException e) {
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("# Snapshot created - " + getSnapshotsNumber() + " snapshots in memory at this time.");
	}

	/**
	 * Given a Snapshot, this method loads the Snapshot's content into the current game.
	 * @param s The Snapshot.
	 */
	public void loadSnapshot(Snapshot s) {
		//instance = s.instance;
		Game.getInstance().getPlayerManager().setPlayers(s.players);
		for (Player p: Game.getInstance().getPlayerManager().getPlayers()) {
			p.setPlayerManager(Game.getInstance().getPlayerManager());
		}
		Game.getInstance().getPlayerManager().setCurrentTurn(s.currentTurn);
		Game.getInstance().getPlayerManager().setTurnNumber(s.turnNumber);
		Game.getInstance().setConfirmingPositions(s.confirmingPositions);
		Game.getInstance().setConfirmingTrain(s.confirmingTrain);
		Game.getInstance().setOrigin(s.origin);
		Game.getInstance().setDestination(s.destination);
		Game.getInstance().setTrainToDestroy(s.trainToDestroy);
		goalManager = s.goalManager;
		resourceManager = s.resourceManager; obstacleManager = s.obstacleManager; map = s.map;
		state = s.state;
		StationController.redrawTrains();
		stateChanged(); // Forcefully triggers an update of the game interface.
	}

	/**
	 * REPLAY METHODS
	 * These can be used to control the Replay functionality of the Game.
	 */

	/**
	 * Is the game in Replay Mode?
	 * When true, no action should be possible but replay navigation.
	 */
	public boolean replayMode = false;

	/**
	 * The snapshot number currently being replayed. Only set if
	 * Replay Mode is true. (e.g. "playing {replayingSnapshot} out of {getSnapshotsNumber()}").
	 */
	public int replayingSnapshot = -1;

	/**
	 * Used for the replay. The overall playing speed.
	 */
	private float gameSpeed = 1.0f;

	public void setGameSpeed(float speedMultiplier) {
		this.gameSpeed = speedMultiplier;
	}

	public float getGameSpeed() {
		return this.gameSpeed;
	}

	/**
	 * This methods returns the current snapshots number.
	 */
	public int getSnapshotsNumber() {
		return this.snapshots.size();
	}

	/**
	 * Navigates to a given point in time.
	 * - If a point in the past is given, ensure Replay Mode is set.
	 * - If the latest point in time is given, ensure Replay Mode is not set.
	 * @param snapshotNumber A number between 0..{getSnapshotsNumber()}
	 */
	public void replaySnapshot(int snapshotNumber) {
		Snapshot s;
		try {
			s = SerializationUtils.deserialize(snapshots.get(snapshotNumber));
		} catch (Exception e) {
			// TODO Catch invalid index exception.
			return;
		}

		loadSnapshot(s);
		replayMode = ( snapshotNumber != getSnapshotsNumber() - 1 );
		replayingSnapshot = replayMode ? snapshotNumber : -1;

		System.out.println("@ Snapshot " + snapshotNumber + " out of " + getSnapshotsNumber() + " has been loaded.");
		System.out.println("  Replay mode " + (replayMode? "is still active.": "has been now deactivated."));

		if (!replayMode) {
			// End of replay actions
			getInstance().setGameSpeed(1.0f);
			ActorsManager.showAllObstacles();
		}

	}


	

	/**The number of players that can play at one time.*/
	private final int CONFIG_PLAYERS = 2;
	
	/**The score a player must reach to win the game.*/
	public final int TOTAL_POINTS = 200;

	/**The Instantiation method, sets up the players and game listeners.*/
	private Game() {
		playerManager = new PlayerManager();
		playerManager.createPlayers(CONFIG_PLAYERS);

		resourceManager = new ResourceManager();
		goalManager = new GoalManager(resourceManager);
		map = new Map();
		obstacleManager = new ObstacleManager(map);
		
		state = GameState.NORMAL;

		playerManager.subscribeTurnChanged(new TurnListener() {
			@Override
			public void changed() {
				Player currentPlayer = playerManager.getCurrentPlayer();
				goalManager.updatePlayerGoals(currentPlayer);
				resourceManager.addRandomResourceToPlayer(currentPlayer);
				resourceManager.addRandomResourceToPlayer(currentPlayer);
				calculateObstacles();
				decreaseObstacleTime();
			}
		});
	}

    public Station getOrigin() {
        return origin;
    }

    public void setOrigin(Station origin) {
        this.origin = origin;
    }

    public Station getDestination() {
        return destination;
    }

    public void setDestination(Station destination) {
        this.destination = destination;
    }

    Station origin, destination;

	/**Returns the main game instance.*/
	public static Game getInstance() {
		if (instance == null) {
			instance = new Game();
			// initialisePlayers gives them a goal, and the GoalManager requires an instance of game to exist so this
			// method can't be called in the constructor
			instance.initialisePlayers();
		}

		return instance;
	}

	/**Sets up the players. Only the first player is given goals and resources initially.*/
	private void initialisePlayers() {
		Player player = playerManager.getAllPlayers().get(0);
		goalManager.updatePlayerGoals(player);
		resourceManager.addRandomResourceToPlayer(player);
		resourceManager.addRandomResourceToPlayer(player);
	}

	/**@return The PlayerManager instance for this game.*/
	public PlayerManager getPlayerManager() {
		return playerManager;
	}

	/**@return The GoalManager instance for this game.*/
	public GoalManager getGoalManager() {
		return goalManager;
	}

	/**@return The ResourceManager instance for this game.*/
	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	/**@return The Map instance for this game.*/
	public Map getMap() {
		return map;
	}

	/**@return The GameState instance for this game.*/
	public GameState getState() {
		return state;
	}

	/**Sets the GameState of the Game. Listeners are notified using stateChanged().*/
	public void setState(GameState state) {
		this.state = state;
		if ( state != GameState.PLACING && state != GameState.ROUTING && state != GameState.WAITING ) {
			this.createSnapshot();
		}
		stateChanged();
	}

	/**Adds a listener for when the game state is changed.*/
	public void subscribeStateChanged(GameStateListener listener) {
		gameStateListeners.add(listener);
	}

	/**When the GameState is changed then all of the GameState Listeners are notified.*/
	private void stateChanged() {
		for(GameStateListener listener : gameStateListeners) {
			listener.changed(state);
		}
	}
	
	/**@return The ObstacleManager instance for this game.*/
	public ObstacleManager getObstacleManager(){
		return obstacleManager;
	}
	
	/**This method is called whenever an obstacle starts. All listeners are notified that this has happened.*/
	private void obstacleStarted(Obstacle obstacle) {
		// called whenever an obstacle starts, notifying all listeners that an obstacle has occured (handled by ... 
		for (ObstacleListener listener : obstacleListeners) {
			listener.started(obstacle);
		}
	}

	/**This method is called whenever an obstacle end. All listeners are notified that this has happened.*/
	private void obstacleEnded(Obstacle obstacle) {
		// called whenever an obstacle ends, notifying all listeners that an obstacle has occured (handled by ... 
		for (ObstacleListener listener : obstacleListeners) {
			listener.ended(obstacle);
		}
	}

	/**This method adds a new ObstacleListener to the game, which is notified when an Obstacle starts or ends.*/
	public void subscribeObstacleChanged(ObstacleListener obstacleListener) {
		obstacleListeners.add(obstacleListener);
	}
	
	/**This method causes one obstacle to happen at random, notifying the listeners.*/
	private void calculateObstacles() {
		// randomly choose one obstacle, then make the obstacle happen with its associated probability
		ArrayList<Tuple<Obstacle, Float>> obstacles = obstacleManager.getObstacles();
		int index = MathUtils.random(obstacles.size()-1);
		
		
		Tuple<Obstacle, Float> obstacleProbPair = obstacles.get(index);
		boolean obstacleOccured = MathUtils.randomBoolean(obstacleProbPair.getSecond());
		Obstacle obstacle = obstacleProbPair.getFirst();
		
		// if it has occurred and isnt already active, start the obstacle
		if(obstacleOccured && !obstacle.isActive()){
			obstacleStarted(obstacle);
		}
	}
	
	/**This method decreases the remaining duration of any remaining obstacles by 1 turn. If the duration has reached 0, the obstacle is removed and all listeners are notified.*/
	private void decreaseObstacleTime() {
		// decreases any active obstacles time left active by 1
		ArrayList<Tuple<Obstacle, Float>> obstacles = obstacleManager.getObstacles();
		for (int i = 0; i< obstacles.size(); i++) {
			Obstacle obstacle = obstacles.get(i).getFirst();
			if (obstacle.isActive()) {
				boolean isTimeLeft = obstacle.decreaseTimeLeft();
				if (!isTimeLeft) {
					// if the time left = 0, then deactivate the obstacle
					obstacleEnded(obstacle);
				}
			}
		}
		
	}

	private void deactivateAllObstacles() {
		ArrayList<Tuple<Obstacle, Float>> obstacles = obstacleManager.getObstacles();
		for (int i = 0; i< obstacles.size(); i++) {
			Obstacle obstacle = obstacles.get(i).getFirst();
			if (obstacle.isActive()) {
				obstacle.end();
			}
		}
	}

}
