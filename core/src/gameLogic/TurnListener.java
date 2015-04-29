package gameLogic;
import java.io.Serializable;

/**This interface adds a listener for when the turn changes.*/
public interface TurnListener extends Serializable {
    public void changed();
}
