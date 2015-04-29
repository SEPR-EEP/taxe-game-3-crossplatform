package test;

import gameLogic.Player;
import gameLogic.PlayerManager;
import gameLogic.resource.ConnectionModifier;
import gameLogic.resource.ResourceManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ResourceManagerTest extends LibGdxTest {

    PlayerManager pm;
    Player player;
    ResourceManager rm;

    @Before
    public void resourceManagerSetup() throws Exception {
        pm = new PlayerManager();
        player = new Player(pm,1);
        rm = new ResourceManager();
    }


    @Test
    public void testAddResourceToPlayer() throws Exception {


        // add enough resources to exceed maximum
        for(int i = 0; i < 20; i++) {
            rm.addRandomResourceToPlayer(player);
        }

        assertTrue(player.getResources().size() == rm.CONFIG_MAX_RESOURCES);
    }

    @Test
    public void testAddConnectionModifierToPlayer() throws  Exception {

        ConnectionModifier connectionModifier = new ConnectionModifier("Connection Modifier", player);
        player.addResource(connectionModifier);

        assertTrue(player.getResources().contains(connectionModifier));
        assertTrue(player.getConnectionModifiers().contains(connectionModifier));
        assertEquals(1, player.getConnectionModifiers().size());
    }
}