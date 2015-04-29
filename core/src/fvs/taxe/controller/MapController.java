package fvs.taxe.controller;

import fvs.taxe.StationClickListener;
import fvs.taxe.actor.ConnectionActor;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.GameStateListener;
import gameLogic.Player;
import gameLogic.map.Connection;
import gameLogic.map.IPositionable;
import gameLogic.map.Station;
import gameLogic.resource.Resource;
import gameLogic.resource.Train;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;


/**
 * @author Stefan
 *Map controller class used to handle map modification - adding
 *and removing connection on the map 
 */
public class MapController {

	/** The context of the game */
	private Context context;

	/**The origin station from which a new connection will be added or
	 * an existing connection will be deleted */
	private Station origin;

	/**The destination station to which a new connection will be added or
	 * an existing connection will be deleted */
	private Station destination;

	/**The width of the connection to be added */
	private final int CONNECTION_LINE_WIDTH = 5;


	/**Instantiation method.
	 * @param context - the context of the game.
	 */
	public MapController(final Context context) {
		this.context = context;
		this.origin = null;
		this.destination = null;

		StationController.subscribeStationClick(new StationClickListener() {
			@Override
			public void clicked(Station station) {
				if (context.getGameLogic().getState().equals(GameState.EDITING)) {
					editConnection(station);
				}
			}
		});

        context.getGameLogic().subscribeStateChanged(new GameStateListener() {
            @Override
            public void changed(GameState state) {
                if ( state != GameState.CONFIRMEDIT ) {
                    return;
                }

				Station o, d;
				o = Game.getInstance().getOrigin();
				d = Game.getInstance().getDestination();

                Boolean success;

                if (context.getGameLogic().getMap().getConnection(o.getName(), d.getName()) == null) {
                    success = addConnection();
                } else {
                    success = removeConnection();
                }

				origin = null; destination = null;
                Game.getInstance().setOrigin(null);
                Game.getInstance().setDestination(null);

                if (success) {
                    context.getGameLogic().setState(GameState.NORMAL);
                } else {
                    context.getGameLogic().setState(GameState.EDITING);
                }
            }
        });
	}

	/**Determines weather a connection should be added or removed and
	 * calls the appropriate method for either adding or removing a connection.
	 * This method is called when a station is clicked.
	 * @param station - the clicked station.
	 */
	public void editConnection(Station station){
		if(origin == null || origin == station){
			origin = station;
		}else{
			destination = station;

            Game.getInstance().setOrigin(origin);
            Game.getInstance().setDestination(destination);
            Game.getInstance().setState(GameState.CONFIRMEDIT);

		}
	}

	/**
	 * Adds a connection to the map and draws it on the GUI, if the connection
	 * does not intersect an existing connection. If the new connection intersects
	 * an existing one the function just returns.
	 */
	private Boolean addConnection(){
        Station origin, destination;
        origin = Game.getInstance().getOrigin();
        destination = Game.getInstance().getDestination();


		Connection connection = new Connection(origin, destination);


		//Check if new connection intersects an existing one
		for(Connection existingConnection : context.getGameLogic().getMap().getConnections()){
			if(connection.intersect(existingConnection)
					&& existingConnection.getStation1()!= destination
					&& existingConnection.getStation2() != origin){
				context.getTopBarController().displayFlashMessage("Invalid connection - cannot intersect another", Color.RED);
				return false;
			}
		}

		System.out.println("Adding a connection between " + connection.getStation1().getName() + " and " + connection.getStation2().getName());

		//Create Actor for the new connection
		final IPositionable start = connection.getStation1().getLocation();
		final IPositionable end = connection.getStation2().getLocation();
		ConnectionActor connectionActor = new ConnectionActor(Color.GRAY, start, end, CONNECTION_LINE_WIDTH);
		connection.setActor(connectionActor);

		//Add connection to Map and Actor to Stage
		context.getGameLogic().getMap().addConnection(connection.getStation1(), connection.getStation2());
		context.getGameLogic().getMap().getConnection(connection.getStation1().getName(), connection.getStation2().getName()).setActor(connectionActor);
		connectionActor.setZIndex(5);
		context.getStage().addActor(connectionActor);

		//Add actors for the stations again so they are on top
		context.getStage().addActor(connection.getStation1().getActor());
		context.getStage().addActor(connection.getStation2().getActor());

		//Add all train actors to the stage again so they are on top
		for(Player player : context.getGameLogic().getPlayerManager().getAllPlayers()){
			for(Resource train : player.getActiveTrains()){
				context.getStage().addActor(((Train)train).getActor());
			}
		}

		return true;
	}

	/**Removes an existing connection from the game map if there are no trains travelling along it.
	 * Causes all trains already on rout through the removed connection to change their final station
	 *  to the one before the removed connection.
	 */
	private Boolean removeConnection(){
        Station origin, destination;
        origin = Game.getInstance().getOrigin();
        destination = Game.getInstance().getDestination();


        for(Player player : context.getGameLogic().getPlayerManager().getAllPlayers()){
			for(Resource resource : player.getActiveTrains()){
				Train train = ((Train) resource);

				//If there is a train on the connection return without removing it
				if( ( train.getMostRecentlyVisitedStation().equals(origin.getName()) && train.getNextStationOfRoute().equals(destination.getName()))
					|| (train.getMostRecentlyVisitedStation().equals(destination.getName()) && train.getNextStationOfRoute().equals(origin.getName()))
					&& train.isMoving()){

					context.getTopBarController().displayFlashMessage("You cannot remove a connection being used by a train", Color.RED);

					return false;

				//If a connection from a trains route is removed the new destination of the train is set to the origin of the removed connection
				}else if(train.getRoute().contains(origin) && train.getRoute().contains(destination) && train.isMoving() &&
                        !train.getHistory().contains(train.getRoute().contains(origin))){
					Station endStation;
					List<Station> route = train.getRoute();

                    System.out.println(train.getSpeed());
                    System.out.println(!train.getHistory().contains(train.getRoute().contains(origin)));


					if(route.indexOf(origin) < route.indexOf(destination)){
						endStation = origin;
					}else{
						endStation = destination;
					}

					Station lastStation = context.getGameLogic().getMap().getStationByName(train.getHistory().get(train.getHistory().size()-1).getFirst());

                    if(route.indexOf(lastStation) < route.indexOf(endStation)){
                    	ArrayList<Station> arrList = new ArrayList<Station>();
                    	arrList.add(route.get(route.indexOf(lastStation)+1));
                        arrList.addAll((new ArrayList(route.subList(route.indexOf(lastStation)+1,
                                route.indexOf(endStation)+1))));
                        train.setRoute(arrList);
                        context.getRouteController().reroute(train);
                    }
                    System.out.println(train.getSpeed());
				}
			}
		}

		Connection  connection = context.getGameLogic().getMap().getConnection(origin.getName(), destination.getName());
		ConnectionActor connectionActor = connection.getActor();

		//Remove actor from game view
		Array<Actor> actors = context.getStage().getActors();
		System.out.println("I removed that actor from the connection.");
		if ( connection.getActor() != null ) {
			connection.getActor().remove();
		}
		connection.setActor(null);

		//Remove connection from game logic
		context.getGameLogic().getMap().removeConnection(origin, destination);

		return true;
	}

}
