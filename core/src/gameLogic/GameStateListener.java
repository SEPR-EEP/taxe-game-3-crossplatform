package gameLogic;
import java.io.Serializable;

/**This interface adds a changed method for when the game state changes to any other class.*/
public interface GameStateListener extends Serializable {
    public void changed(GameState state);
}
