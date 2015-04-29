package gameLogic.map;

import Util.HasActor;
import fvs.taxe.actor.ConnectionActor;

/**A connection describes the link between 2 stations.*/
public class Connection extends HasActor<ConnectionActor> {
	/**The first station of the connection.*/
	private Station station1;
	
	/**The second station of the connection.*/
	private Station station2;

	/**Instantiation method.
	 * @param station1 The first station for the connection.
	 * @param station2 The second station for the connection.
	 */
	public Connection(Station station1, Station station2) {
		this.station1 = station1;
		this.station2 = station2;
	}
	
	/**@return The first station used in this connection.*/
	public Station getStation1() {
		return this.station1;
	}

	/**@return The second station used in this connection.*/
	public Station getStation2() {
		return this.station2;
	}

	/**Determines whether two lines intersect given their coordinates.
	 * @param otherConnection Other connection to test intersection
	 * @return - true if the lines intersect false otherwise
	 */
	public boolean intersect(Connection otherConnection){

		//Get coordinates of this connection
		IPositionable a = this.getStation1().getLocation();
		IPositionable b = this.getStation2().getLocation();

		//Get coordinates of the other connection
		IPositionable c = otherConnection.getStation1().getLocation();
		IPositionable d = otherConnection.getStation2().getLocation();

		return counterClockWise(a,c,d) != counterClockWise(b,c,d) && counterClockWise(a,b,c) != counterClockWise(a,b,d);
	}

	/**Determines whether or not three points are arranged counterclockwise on
	 * a plane given their coordinates. This method is used by the intersect method.
	 * @param a - coordinate of the first point
	 * @param b - coordinate of the second point
	 * @param c - coordinate of the third point
	 * @return - true if the three point are arranged counterclockwise and false otherwise
	 */
	private boolean counterClockWise(IPositionable a, IPositionable b, IPositionable c){
		return (c.getY() - a.getY())*(b.getX() - a.getX()) > (b.getY()-a.getY())*(c.getX() - a.getX());
	}

}
