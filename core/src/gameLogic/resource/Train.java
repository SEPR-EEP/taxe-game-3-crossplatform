package gameLogic.resource;

import Util.Tuple;
import fvs.taxe.actor.TrainActor;
import gameLogic.map.IPositionable;
import gameLogic.map.Station;

import java.util.ArrayList;
import java.util.List;

/** The class that represents a train- defined by its name and speed */
public class Train extends Resource {
	/** The string location of the png file that represents this train when moving to the left */
    private String leftImage;
    
    /** The string location of the png file that represents this train when moving to the right */
    private String rightImage;
    
    /** The position that the Train is located at */
    private IPositionable position;
    
    /** The TrainActor that is associated with the Train object */
    private TrainActor actor;
    
    /** The number of pixels the train will move per turn */
    private int speed;
    
    /** If the train is set on a route, the final station in that route, otherwise null*/
    private Station finalDestination;
    
    /** If a route has been set for the train, the list of stations that make up that route 
     * (starting from station after start station */
    private List<Station> route;
    
    /** The history of where the train has travelled- list of Station names 
     * and the turn number they arrived at that station */
    private List<Tuple<String, Integer>> history;

    /** Constructor for train initialises the names, images, speed, history and route
     * @param name The string that represents this train
     * @param leftImage The file name in assets/trains that corresponds to the left image
     * @param rightImage The file name in assets/trains that corresponds to the right image
     * @param speed The number of pixels the train moves per turn
     */
    public Train(String name, String leftImage, String rightImage, int speed) {
        this.name = name;
        this.leftImage = leftImage;
        this.rightImage = rightImage;
        this.speed = speed;
        history = new ArrayList<Tuple<String, Integer>>();
        route =  new ArrayList<Station>();
    }
    
    /** Get the name of the train
     * @return String that represents the Train
     */
    public String getName() {
    	return name;
    }

    /** Get the filepath associated with the image of the train when moving left 
     * @return String representing filepath of left image, in assets/ directory
     */
    public String getLeftImage() {
        return "trains/" + leftImage;
    }

    /** Get the filepath associated with the image of the train when moving right 
     * @return String representing filepath of right image, in assets/ directory
     */
    public String getRightImage() {
        return "trains/" + rightImage;
    }

    /** Get the filepath associated with the cursor image of the train
     * @return String representing filepath of cursor image, in assets/ directory
     */
    public String getCursorImage() {
        return "trains/cursor/" + leftImage;
    }

    /** Set the position of the train to be the Ipositionable given
     * @param position The Ipositionable position the Train will be set to
     */
    public void setPosition(IPositionable position) {
        this.position = position;
        changed();
    }

    /** Get the position that the train is currently located
     * @return The position the Train is currently set to
     */
    public IPositionable getPosition() {
        return position;
    }

    /** Set the Train to correspond to the given TrainActor 
     * @param actor The actor that represents this train
     */
    public void setActor(TrainActor actor) {
        this.actor = actor;
    }

    /** Get the actor that represents this train
     * @return The actor that represents the train, or null if none set
     */
    public TrainActor getActor() {
        return actor;
    }

    /** Set the route (represented at list of stations) of the train to be the given route and set the finalDestination to be last station in route
     * @param route Route that the train will take (as a list of stations)
     */
    public void setRoute(List<Station> route) {
        if (route != null && route.size() > 0){
        	finalDestination = route.get(route.size() - 1);
        }
        this.route = route;
    }

    /** Return whether the train is currently moving
     * @return True if the train is moving or False if train is not moving
     */
    public boolean isMoving() {
        return finalDestination != null;
    }

    /** Return the train's route
     * @return The route that the train is currently on, or null if none set
     */
    public List<Station> getRoute() {
        return route;
    }

    /** Get the final destination in the route the train is currently on
     * @return The last station in the route the train is set to, null if no finalDestination set
     */
    public Station getFinalDestination() {
        return finalDestination;
    }

    /** Set the final destination to be the given station
     * @param station The station that will be set as the finalDestination
     */
    public void setFinalDestination(Station station) {
        finalDestination = station;
    }
    
    /** Get the speed that is associated with the train
     * @return The number of pixels that the train will move per turn
     */
    public int getSpeed() {
        return speed;
    }

    /** Get the history of the train
     * @return The list of pairs of stations and the turn number they arrived at the station
     */
    public List<Tuple<String, Integer>> getHistory() {
        return history;
    }

    /** Add a new history pairing of station and the turn the station was arrived at
     * @param stationName The name of the station that the train has arrived at
     * @param turn What turn number the train arrived at that given station
     */
    public void addHistory(String stationName, int turn) {
        history.add(new Tuple<String, Integer>(stationName, turn));
    }

    @Override
    public void dispose() {
        if (actor != null) {
            actor.remove();
        }
    }
}
