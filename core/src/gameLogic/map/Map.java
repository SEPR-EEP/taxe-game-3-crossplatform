package gameLogic.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import Util.Node;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class Map {
	/**The stations that exist on the map.*/
    private List<Station> stations;
    
    /**The connections that exist between stations on the map.*/
    private List<Connection> connections;
    
    /**Random used for random number generation.*/
    private Random random = new Random();

    /**Instantiation, calls the initialise method.*/
    public Map() {
        stations = new ArrayList<Station>();
        connections = new ArrayList<Connection>();
        initialise();
    }

    /**The initialise method loads the json files and creates the stations and connections with parseStations and parseConnections.*/
    private void initialise() {
        JsonReader jsonReader = new JsonReader();
        JsonValue jsonVal = jsonReader.parse(Gdx.files.local("stations.json"));

        parseStations(jsonVal);
        parseConnections(jsonVal);
    }

    /**This method takes a JsonValue and produces an array of connections for use in the map.
     * @param jsonVal The Json data to use.
     */
    private void parseConnections(JsonValue jsonVal) {
        for(JsonValue connection = jsonVal.getChild("connections"); connection != null; connection = connection.next) {
            String station1 = "";
            String station2 = "";

            for(JsonValue val = connection.child; val != null; val = val.next) {
                if(val.name.equalsIgnoreCase("station1")) {
                    station1 = val.asString();
                } else {
                    station2 = val.asString();
                }
            }

            addConnection(station1, station2);
        }
    }
    
    /**This method takes a JsonValue and produces an array of stations for use in the map.
     * @param jsonVal The Json data to use.
     */
    private void parseStations(JsonValue jsonVal) {
        for(JsonValue station = jsonVal.getChild("stations"); station != null; station = station.next) {
            String name = "";
            int x = 0;
            int y = 0;
            boolean isJunction = false;

            for(JsonValue val = station.child; val != null; val = val.next) {
                if(val.name.equalsIgnoreCase("name")) {
                    name = val.asString();
                } else if(val.name.equalsIgnoreCase("x")) {
                    x = val.asInt();
                } else if(val.name.equalsIgnoreCase("y")) {
                    y = val.asInt();
                } else {
                    isJunction = val.asBoolean();
                }
            }

            if (isJunction) {
                addJunction(name, new Position(x,y));
            } else {
                addStation(name, new Position(x, y));
            }
        }
    }

    /**This method checks whether a connection exists between 2 stations.
     * @param stationName The first station.
     * @param anotherStationName The second station.
     * @return True if there is a connection, false otherwise.
     */
    public boolean doesConnectionExist(String stationName, String anotherStationName) {
        for (Connection connection : connections) {
            String s1 = connection.getStation1().getName();
            String s2 = connection.getStation2().getName();

            if (s1.equals(stationName) && s2.equals(anotherStationName)
                || s1.equals(anotherStationName) && s2.equals(stationName)) {
                return true;
            }
        }

        return false;
    }

    /**This method returns the connection between 2 stations.
     * @param stationName The first station.
     * @param anotherStationName The second station.
     * @return The connection between stations if one exists, null otherwise.
     */
    public Connection getConnection(String stationName, String anotherStationName) {
        for (Connection connection : connections) {
            String s1 = connection.getStation1().getName();
            String s2 = connection.getStation2().getName();

            if (s1.equals(stationName) && s2.equals(anotherStationName)
                || s1.equals(anotherStationName) && s2.equals(stationName)) {
                return connection;
            }
        }

        return null;
    }
    
    /**This method picks a random station from the Array of stations.*/
    public Station getRandomStation() {
        return stations.get(random.nextInt(stations.size()));
    }

    /**This method adds a new Station to the game.
     * @param name The name of the new station.
     * @param location The position of the new station in the game.
     * @return The newly added station.
     */
    public Station addStation(String name, Position location) {
        Station newStation = new Station(name, location);
        stations.add(newStation);
        return newStation;
    }
    
    /**This method adds a new Junction to the game.
     * @param name The name of the new Junction.
     * @param location The position of the new Junction in the game.
     * @return The newly added junction.
     */
    public CollisionStation addJunction(String name, Position location) {
    	CollisionStation newJunction = new CollisionStation(name, location);
    	stations.add(newJunction);
    	return newJunction;
    }

    /**@return The list of stations that exist in the game.*/
    public List<Station> getStations() {
        return stations;
    }

    /**@return The list of connections that exist in the game.*/
    public List<Connection> getConnections() {
        return connections;
    }

    /**This method adds a new connection between 2 stations to the game.
     * @param station1 The first station used in the connection.
     * @param station2 The second station used in the connection.
     * @return The newly created connection.
     */
    public Connection addConnection(Station station1, Station station2) {
        Connection newConnection = new Connection(station1, station2);
        connections.add(newConnection);
        return newConnection;
    }

    /**This method replicated addConnection but allows the use of station names instead of objects.
     * @param station1 The name of the first station.
     * @param station2 The name of the second station.
     * @return The newly created connection.
     */
    public Connection addConnection(String station1, String station2) {
        Station st1 = getStationByName(station1);
        Station st2 = getStationByName(station2);
        return addConnection(st1, st2);
    }

    /**This method gets all of the connections from a station.
     * @param station The station to get connections from.
     * @return The list of connections that connect to the parameter station.
     */
    public List<Connection> getConnectionsFromStation(Station station) {
        List<Connection> results = new ArrayList<Connection>();
        for(Connection connection : connections) {
            if(connection.getStation1() == station || connection.getStation2() == station) {
                results.add(connection);
            }
        }
        return results;
    }
    
    /**This method gets all of the connected stations to a station, using a list of available stations to pick from.
     * @param station The target station.
     * @param availableStations The list of stations the station could be connected to.
     * @return The list of stations that the target station is connected to.
     */
    public ArrayList<Station> getConnectedStations(Station station, List<Station> availableStations)
	{
		ArrayList<Station> connectedStations = new ArrayList<Station>();
		for(Connection c : getConnectionsFromStation(station))
		{
			//Establish which end of the connection is the discovered station
			Station discoveredStation;
			if(c.getStation1().equals(station))
			{
				discoveredStation = c.getStation2();
			}
			else
			{
				discoveredStation = c.getStation1();
			}
			//Add the station if it is in the given list of stations or if the list of stations is empty
			if(availableStations != null)
			{
				if(availableStations.contains(discoveredStation))
				{
					availableStations.remove(discoveredStation);
					connectedStations.add(discoveredStation);
				}
			}
			else
			{
				connectedStations.add(discoveredStation);
			}
		}
		return connectedStations;
	}
    
    /**This method calculates the distance between 2 stations.
     * @param station1 The first station.
     * @param station2 The second station.
     * @return The float distance between the 2 stations.
     */
    public float getDirectDistanceBetweenStations(Station station1, Station station2)
    {
    	return (float)Math.sqrt(Math.pow(station1.getLocation().getX() - station2.getLocation().getX(), 2) + Math.pow(station1.getLocation().getY() - station2.getLocation().getY(), 2));
    }

    /**This method finds a station by it's name.
     * @param name The name of the station to be found.
     * @return The station, if found, null otherwise.
     */
    public Station getStationByName(String name) {
        int i = 0;
        while(i < stations.size()) {
            if(stations.get(i).getName().equals(name)) {
                return stations.get(i);
            } else{
                i++;
            }
        }
        return null;
    }

    /**This method finds a station by it's position.
     * @param position The position of the station to be found.
     * @return The station, if found, throws an error otherwise.
     */
    public Station getStationFromPosition(IPositionable position) {
        for (Station station : stations) {
            if (station.getLocation().equals(position)) {
                return station;
            }
        }

        throw new RuntimeException("Station does not exist for that position");
    }

    /**This method creates a route using a list of positions.
     * @param positions The list of positions that the route consists of.
     * @return The list of stations that make up the route.
     */
    public List<Station> createRoute(List<IPositionable> positions) {
        List<Station> route = new ArrayList<Station>();

        for (IPositionable position : positions) {
            route.add(getStationFromPosition(position));
        }

        return route;
    }

    /**@return A clone of the stations in the game for use in routing.*/
	public List<Station> getStationsList() {
		ArrayList<Station> ret = new ArrayList<Station>();
		for(Station s : getStations())
		{
			ret.add(s);
		}
		return ret;
	}
	
	/**This method applies an A* Search graph to find the ideal route between 2 stations.
	 * @param destination The destination station of the route.
	 * @param fringe The fringe contains the list of stations to be explored. Initially this should be an ArrayList<Node<Station>> containing only a single node
	 * who'se data is the origin station of the route.
	 * @param availableStations The list of stations that can be used in routing.
	 * @return The list of stations that make up the route.
	 */
	public List<Station> getIdealRoute(final Station destination, ArrayList<Node<Station>> fringe, List<Station> availableStations)
	{
		//Apply an A* heuristic algorthim to find the ideal route
		//Items in the fringe are a tuple pair of stations and the distance *so far* to that station
		
		//If the fringe is empty or the unusedStations array is empty we have checked as far as we can and must return
		if(fringe.isEmpty() || availableStations.isEmpty())
		{
			return null;
		}
		
		//<---------SORTING: Sort the fringe--------->
		//Firstly sort the fringe using our heuristic
		Collections.sort(fringe, new Comparator<Node<Station>>() {
		       @Override
		       public int compare(Node<Station>  station1, Node<Station>  station2)
		       {
		    	   //We sort our stations based on the shortest estimated total path, using the total path so far and the estimated path to the destination
		       		float fNStation1 = station1.getNodeCost() + h(station1.getData());
		       		float fNStation2 = station2.getNodeCost() + h(station2.getData());
		       		return  Float.compare(fNStation1, fNStation2);
		       }
		       
		       //Heuristic function h(n)
			   public float h(Station station)
			   {
			      	//Our heuristic is the direct distance from the station to the destination
			      	return getDirectDistanceBetweenStations(station, destination);
			   }
			   });
				
		//<---------EXPANDING: Expand the first fringe station and check for goal--------->
		//Firstly get the stations produced from expanding the first fringe station
		Node<Station> stationExpanded = fringe.get(0);
		fringe.remove(0);
		fringe.trimToSize();
		ArrayList<Station> expandedStations = getConnectedStations(stationExpanded.getData(), availableStations);
		for(Station newStation : expandedStations)
		{
			//Check goal criteria
			if(newStation.equals(destination))
			{
				//If we have found the goal station, iterate back up the tree to produce a route
				List<Station> lst = new ArrayList<Station>();
				lst.add(newStation);
				lst.add(stationExpanded.getData());
				if(stationExpanded.hasParent())
				{
					Node<Station> parentStation = stationExpanded.getParent();
					lst.add(parentStation.getData());
					while(parentStation.hasParent())
					{
						parentStation = parentStation.getParent();
						lst.add(parentStation.getData());
					}
				}
				return lst;
			}
			else
			{
				//If the station is not a goal station, add it to the fringe and the graph
				Node<Station> newNode = new Node<Station>();
				newNode.setData(newStation);
				newNode.setNodeCost(stationExpanded.getNodeCost() + getDirectDistanceBetweenStations(stationExpanded.getData(), newStation));
				newNode.setParent(stationExpanded);
				fringe.add(newNode);
			}
		}
		
		//If we have not found a route, perform the next iteration of the A* search
		return getIdealRoute(destination, fringe, availableStations);
	}

	/**This method finds the length of a route.
	 * @param idealRoute The list of stations that make up a route.
	 * @return The length of the route.
	 */
	public float getRouteLength(List<Station> idealRoute) {
		//Simple method for finding the length of a route
		int i = 1;
		float length = 0.0f;
		Station previousStation = idealRoute.get(0);
		while(i < idealRoute.size())
		{
			//Iterate through the list adding up the length
			length = length + this.getDirectDistanceBetweenStations(idealRoute.get(i), previousStation);
			previousStation = idealRoute.get(i);
			i++;
		}
		return length;
	}

	/**This method returns a route with all irreplacable stations removed. This is useful for finding stations that can be avoided in goals.
	 * @param route The list of stations that make up the original route.
	 * @return The route with irreplacable stations removed.
	 */
	public List<Station> getEditableRoute(List<Station> route) {
		ArrayList<Station> editableRoute = new ArrayList<Station>();
		int i = 1;
		Station origin = route.get(0);
		Station destination = route.get(route.size() - 1);
		while(i < route.size() - 1)
		{
			Node<Station> originNode = new Node<Station>();
			originNode.setData(origin);
			ArrayList<Node<Station>> searchFringe = new ArrayList<Node<Station>>();
			searchFringe.add(originNode);
			List<Station> stations = getStationsList();
			stations.remove(route.get(i));
			List<Station> idealRoute = getIdealRoute(destination, searchFringe, stations);
			//Check for a route without this station. If it exists, then this station is editable.
			if(idealRoute != null)
			{
				editableRoute.add(route.get(i));
			}
			i++;
		}
		return editableRoute;
	}
}
