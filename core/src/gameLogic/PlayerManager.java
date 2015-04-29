package gameLogic;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

/**This class creates and manages players.*/
public class PlayerManager implements Serializable {


	/**The active players in this instance of PlayerManager.*/
	private ArrayList<Player> players = new ArrayList<Player>();

	/**The turn the game has reached.*/
	private int turnNumber = 0;

	/**Which player's turn it is, represented by 1 or 0.*/
	private int currentTurn = 0;

	public int getCurrentTurn() {
		return currentTurn;
	}

	public void setCurrentTurn(int currentTurn) {
		this.currentTurn = currentTurn;
	}

	public void setTurnNumber(int turnNumber) {
		this.turnNumber = turnNumber;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}

	/**The listeners attached to this PlayerManager that are notified every time the turn changes.*/
	private List<TurnListener> turnListeners = new ArrayList<TurnListener>();
	
	/**The listeners attached to this PlayerManager that are notified every time the Player's goals or resources are changed.*/
	private List<PlayerChangedListener> playerListeners = new ArrayList<PlayerChangedListener>();
	
	/**This method creates a certain number of players and adds them to the players array.
	 * @param count The number of players to create.
	 */
	public void createPlayers(int count) {
		for (int i = 0; i < count; i++) {
			players.add(new Player(this, i+1));
		}
	}
	
	/**@return The current player in the game.*/
	public Player getCurrentPlayer() {
		return players.get(currentTurn);
	}

	/**@return The list of players in the game.*/
	public List<Player> getAllPlayers() {
		return players;
	}
	
	/**This method is called every time a turn is completed. currentTurn is updated, and both turnChanged and playerChanged are called.*/
	public void turnOver() {
		currentTurn = currentTurn == 1 ? 0 : 1;
		turnChanged();
		playerChanged();
	}

	/**This method adds a new TurnListener to the game that will be updated each time the turn changes.
	 * @param listener The TurnListener to be added.
	 */
	public void subscribeTurnChanged(TurnListener listener) {
		turnListeners.add(listener);
	}

	/**This method is called each time the turn changes. It incremembers the turnNumber and notifies all of the turn listeners.*/
	private void turnChanged() {
		turnNumber++;
		// reverse iterate to give priority to calls from Game() (obstacles)
		for(int i = 0; i< turnListeners.size(); i++) {
			turnListeners.get(turnListeners.size()-1-i).changed();
		}
	}

	/**This method adds a new PlayerChangedListener to the game that will be updated each time the turn changes or the player's resources or goals are updated.*/
	public void subscribePlayerChanged(PlayerChangedListener listener) {
		playerListeners.add(listener);
	}

	/**This method is called when the player's resources, goals or turn change. It notifies all of the PlayerChangedListeners in this PlayerManager instance.*/
	public void playerChanged() {
		for (PlayerChangedListener listener : playerListeners) {
			listener.changed();
		}
	}

	/**@return The current number of turns that have passed.*/
	public int getTurnNumber() {
		return turnNumber;
	}
}
