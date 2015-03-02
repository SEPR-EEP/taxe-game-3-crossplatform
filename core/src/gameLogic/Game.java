package gameLogic;

import gameLogic.goal.GoalManager;
import gameLogic.map.Map;
import gameLogic.obstacle.Obstacle;
import gameLogic.obstacle.ObstacleListener;
import gameLogic.obstacle.ObstacleManager;
import gameLogic.resource.ResourceManager;

import java.util.ArrayList;
import java.util.List;

import Util.Tuple;

import com.badlogic.gdx.math.MathUtils;

/**Main Game class of the Game. Handles all of the game logic.*/
public class Game {
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
}
