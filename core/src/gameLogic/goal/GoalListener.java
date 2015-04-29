package gameLogic.goal;

import java.io.Serializable;

/**This interface can be added to a class to allow it to listen for when a goal has finished.*/
public interface GoalListener extends Serializable {
	public void finished(Goal goal);
}
