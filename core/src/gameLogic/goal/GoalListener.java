package gameLogic.goal;

/**This interface can be added to a class to allow it to listen for when a goal has finished.*/
public interface GoalListener {
	public void finished(Goal goal);
}
