package test;

import gameLogic.Game;
import gameLogic.Player;
import gameLogic.PlayerManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GameTest extends LibGdxTest {
    private PlayerManager pm;
    private Game game;
    private Player p1;

    @Before
    public void setUpGame() throws Exception {
        game = Game.getInstance();
        game.getPlayerManager();
        pm = game.getPlayerManager();
        p1 = pm.getCurrentPlayer();
    }

    @Test
    public void testPlayerChanged() throws Exception {

        assertEquals("Player should start with two resources", 2, p1.getResources().size());
        assertEquals("Player should start with three goals", 3, p1.getNumberOfIncompleteGoals());

        pm.turnOver();
        pm.turnOver();

        // resource count should increase when p1 has another turn
        assertEquals("Player should now have four resources", 4, p1.getResources().size());

        // goal count
        assertEquals("Player should still have maximum of three goals", 3, p1.getNumberOfIncompleteGoals());

        pm.turnOver();
        pm.turnOver();

        // resource count should increase when p1 has another turn
        assertEquals("Player should now have six resources", 6, p1.getResources().size());

        // goal count
        assertEquals("Player should still have maximum of three goals", 3, p1.getNumberOfIncompleteGoals());

        pm.turnOver();
        pm.turnOver();

        // resource count should increase when p1 has another turn
        assertEquals("Player should have maximum of seven resources", 7, p1.getResources().size());

        // goal count
        assertEquals("Player should still have maximum of three goals", 3, p1.getNumberOfIncompleteGoals());



    }

}