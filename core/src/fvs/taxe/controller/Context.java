package fvs.taxe.controller;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import fvs.taxe.TaxeGame;
import gameLogic.Game;
import gameLogic.map.Station;

/**This class stores the Context of the game, such as the game itself, the stage, etc.*/
public class Context {
	
	/**The main Instance of the game is storedhere.*/
    private TaxeGame taxeGame;
    
    /**The stage of the game is stored here.*/
    private Stage stage;
    
    /**The skin used for UI in the game is stored here.*/
    private Skin skin;
    
    /**The Game logic itself is stored here.*/
    private Game gameLogic;
    
    /**A RouteController for the context that can be get or set.*/
    private RouteController routeController;
    
    /**A TopBarController for the context that can be get or set.*/
    private TopBarController topBarController;

    /**A MapController for the context that can be get or set.*/
    private MapController mapController;

    /**A GoalController for the context that can be get or set.*/
    private GoalController goalController;

    /**The station controller*/
    private StationController stationController;

    /**Instantiation method sets up private variables.
     * @param stage The stage to be used in the context
     * @param skin The skin to be used in the context
     * @param taxeGame The main Game instance to be used in the context
     * @param gameLogic The Game's logic instance to be used in the context
     */
    public Context(Stage stage, Skin skin, TaxeGame taxeGame, Game gameLogic) {
        this.stage = stage;
        this.skin = skin;
        this.taxeGame = taxeGame;
        this.gameLogic = gameLogic;
    }

    /**@returns the Context's stage.*/
    public Stage getStage() {
        return stage;
    }

    /**@returns the Context's skin.*/
    public Skin getSkin() {
        return skin;
    }

    /**@returns the Context's main Game Instance.*/
    public TaxeGame getTaxeGame() {
        return taxeGame;
    }

    /**@returns the Context's Game Logic.*/
    public Game getGameLogic() {
        return gameLogic;
    }

    /**@returns the Context's RouteController.*/
    public RouteController getRouteController() {
        return routeController;
    }

    /**Sets the routeController.
     * @param routeController The new RouteController to be used in the Context.
     */
    public void setRouteController(RouteController routeController) {
        this.routeController = routeController;
    }

    /**@returns the Context's TopBarController.*/
    public TopBarController getTopBarController() {
        return topBarController;
    }
    /**Sets the TopBarController.
     * @param topBarController The new TopBarController to be used in the Context.
     */
    public void setTopBarController(TopBarController topBarController) {
        this.topBarController = topBarController;
    }

    /**
     * Sets the goalController
     * @param goalController The new goalController to be used in the Context.
     */
    public void setGoalController(GoalController goalController){ this.goalController = goalController; }

    /** @returns the Context's goalController */
    public GoalController getGoalController() { return goalController; }

    /**
     * Sets the mapController
     * @param mapController The new mapController to be used in the Context.
     */
    public void setMapController(MapController mapController) { this.mapController = mapController; }

    /** @returns the Context's mapController */
    public MapController getMapController() { return mapController; }

    public StationController getStationController() {
        return stationController;
    }

    public void setStationController(StationController stationController) {
        this.stationController = stationController;
    }

}
