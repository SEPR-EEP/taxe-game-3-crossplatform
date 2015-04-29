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
public class GoalDeleteClicked extends ClickListener {

    /**The game context.*/
    private Context context;

    /**The goal the ClickListener represents.*/
    private Goal goal;

    /**Instantiation method.
     * @param goal The goal that the ClickListener corresponds to.
     */
    public GoalDeleteClicked(Context context, Goal goal) {
        this.context = context;
        this.goal = goal;
    }

    /**When the GoalClicked is clicked, then the origin and destination of the goal are highlighted on the map.*/
    @Override
    public void clicked(InputEvent event, float x, float y) {

        if (Game.getInstance().getState() != GameState.NORMAL) return;

        // current player can't be passed in as it changes so find out current player at this instant
        Player currentPlayer = Game.getInstance().getPlayerManager().getCurrentPlayer();

        GoalDialogButtonClicked listener = new GoalDialogButtonClicked(context, currentPlayer, goal);
        DialogGoal dia = new DialogGoal(context, goal, context.getSkin());
        dia.show(context.getStage());
        dia.subscribeClick(listener);
    }

}
