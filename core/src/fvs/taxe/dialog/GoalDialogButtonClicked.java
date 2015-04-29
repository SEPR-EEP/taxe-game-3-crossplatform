package fvs.taxe.dialog;

import fvs.taxe.Button;
import fvs.taxe.controller.Context;
import gameLogic.Player;
import gameLogic.goal.Goal;

/**
 * Created by Team EEP
 */
public class GoalDialogButtonClicked implements GoalDialogClickListener {

    /**The context of the game.*/
    private Context context;

    /**The current player in the game*/
    private Player currentPlayer;

    /**The goal the Dialogue represents.*/
    private Goal goal;

    /**Instantiation
     * @param context The game Context.
     * @param player The current player for the dialogue.
     * @param goal The goal for the dialogue.
     */
    public GoalDialogButtonClicked(Context context, Player player, Goal goal) {
        this.currentPlayer = player;
        this.goal = goal;
        this.context = context;
    }

    @Override
    public void clicked(Button button){
        switch (button){
            case GOAL_DELETE:
                //Set the goal as complete without giving player a reward
                goal.setComplete();
                //Redraw list of goals
                context.getGoalController().drawCurrentPlayerGoals();
                break;
        }
    }
}
