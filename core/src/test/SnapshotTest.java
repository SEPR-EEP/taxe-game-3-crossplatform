package test;

import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.Player;
import gameLogic.PlayerManager;
import gameLogic.map.IPositionable;
import gameLogic.map.Position;
import gameLogic.map.Station;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SnapshotTest extends LibGdxTest {
    private PlayerManager pm;
    private Game game;
    private Player p1;
    private Station a;

    @Before
    public void setUpGame() throws Exception {
        game = Game.getInstance();
        game.getPlayerManager();
        pm = game.getPlayerManager();
        p1 = pm.getCurrentPlayer();
        a = new Station("Rome", new Position(100, 200));
    }

    @Test
    public void testSnapshots() throws Exception {

        game.setDestination(a);
        assertTrue("The new destination is set for this state", game.getDestination().equals(a));

        game.setState(GameState.NORMAL);
        assertTrue("Game State is initally set to NORMAL", game.getState() == GameState.NORMAL);

        game.setState(GameState.ANIMATING);
        assertTrue("Game State is now set to WAITING", game.getState() == GameState.ANIMATING);

        assertTrue("There should be 2 snapshots of the Game already.", game.getSnapshotsNumber() == 2);

        game.setDestination(null);
        assertTrue("The new destination is NOT for this new state", game.getDestination() == null);

        assertTrue("You are now NOT in replay mode", !game.replayMode);

        game.replaySnapshot(0);
        assertTrue("Game state is now again NORMAL", game.getState() == GameState.NORMAL);

        assertTrue("The  destination is set for this state ", game.getDestination().equals(a));

        assertTrue("You are now in replay mode", game.replayMode);

    }

    @Test
    public void testSetReplaySpeed() throws Exception {

        assertEquals("Game speed should start at 1.0f", 1.0f, game.getGameSpeed(), 0);

        //Set the speed to five times faster
        game.setGameSpeed(5);

        assertEquals("Game speed should now be five times faster", 5.0f, game.getGameSpeed(), 0);

        //Set the speed to a quarter of original
        game.setGameSpeed(0.25f);

        assertEquals("Game speed should now be a quarter", 0.25f, game.getGameSpeed(), 0);

    }

}