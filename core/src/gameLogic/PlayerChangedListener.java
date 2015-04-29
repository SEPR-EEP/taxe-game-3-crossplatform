package gameLogic;
import java.io.Serializable;

/**This interface adds a listener for when the player changes to a class.*/
public interface PlayerChangedListener extends Serializable {
    public void changed();
}
