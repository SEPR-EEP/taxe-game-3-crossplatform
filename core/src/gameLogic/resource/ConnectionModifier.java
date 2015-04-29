package gameLogic.resource;

import gameLogic.Player;

/** The class that represents a "token" resource that can be spent to
 * allow the player to make one modification to the map's connections
 * @author Team EEP
 */
public class ConnectionModifier extends Resource {

    /** Constructor for the ConnectionModifier initialises the name and associated player
    * @param name ConnectionModifier name
    * @param player Player who owns ConnectionModifier
     */
    public ConnectionModifier(String name, Player player){
        this.name = name;
        this.setPlayer(player);
    }

    @Override
    public void dispose() {
    }
}
