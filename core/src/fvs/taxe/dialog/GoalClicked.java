package fvs.taxe.dialog;

import fvs.taxe.controller.Context;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.Player;
import gameLogic.goal.Goal;
import gameLogic.map.Station;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**Specialised dialogue which is activated when a goal is clicked.*/
public class GoalClicked extends ClickListener {

	/**The game context.*/
	private Context context;

	/**The goal the ClickListener represents.*/
	private Goal goal;

	/**Instantiation method.
	 * @param goal The goal that the ClickListener corresponds to.
	 */
	public GoalClicked(Context context, Goal goal) {
		this.context = context;
		this.goal = goal;
	}

	/**When the GoalClicked is clicked, then the origin and destination of the goal are highlighted on the map.*/
	@Override
	public void clicked(InputEvent event, float x, float y) {
		// if a goal is selected, show the stations as 'selected'
		final Station origin = goal.getOrigin();
		final Station dest = goal.getDestination();
	
		origin.getActor().selected();
		dest.getActor().selected();

	}

}
