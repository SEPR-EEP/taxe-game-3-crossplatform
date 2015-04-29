package fvs.taxe.dialog;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import fvs.taxe.Button;
import fvs.taxe.controller.Context;
import gameLogic.GameState;
import gameLogic.goal.Goal;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by Team EEP
 */
public class DialogGoal extends Dialog {

    private List<GoalDialogClickListener> clickListeners = new ArrayList<GoalDialogClickListener>();

    private Context context;

    public DialogGoal(Context context, Goal goal, Skin skin){
        super(goal.toString(), skin);
        this.context = context;
        context.getGameLogic().setState(GameState.WAITING);

        text("What do you want to do with this goal?");

        button("Cancel", "CLOSE");
        button("Delete", "DELETE");
    }

    public Dialog show (Stage stage){
        show(stage, null);
        setPosition(Math.round((stage.getWidth() - getWidth()) / 2), Math.round((stage.getHeight() - getHeight()) / 2));
        return this;
    }

    @Override
    public void hide () {
        hide(null);
    }

    /**This method is called when a button is clicked. All of the registered click listeners are notified.*/
    private void clicked(Button button) {
        for(GoalDialogClickListener listener : clickListeners) {
            listener.clicked(button);
        }
    }

    /**This method adds a new ClickListener. When a button is clicked, the new listener will be notified.*/
    public void subscribeClick(GoalDialogClickListener listener) {
        clickListeners.add(listener);
    }

    @Override
    protected void result(Object obj) {
        context.getGameLogic().setState(GameState.NORMAL);
        if (obj == "CLOSE") {
            this.remove();
        } else if (obj == "DELETE") {
            clicked(Button.GOAL_DELETE);
        }

    }
}
