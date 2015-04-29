package test;

import gameLogic.map.Connection;
import gameLogic.map.Map;
import gameLogic.map.Position;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MapTest extends LibGdxTest{
    private Map map;

    String name1 = "station1";
    String name2 = "station2";
    String name3 = "station3";
    String name4 = "station4";

    @Before
    public void mapSetup() throws Exception {
        map = new Map();
    }

    @Test
    public void addStationAndConnectionTest() throws Exception {
        int previousSize = map.getStations().size();

        map.addStation(name1, new Position(9999, 9999));
        map.addStation(name2, new Position(200,200));

        assertTrue("Failed to add stations", map.getStations().size() - previousSize == 2);

        map.addConnection(name1, name2);
        assertTrue("Connection addition failed", map.doesConnectionExist(name2,  name1));

        // Should throw an error by itself
        map.getStationFromPosition(new Position(9999, 9999));
    }

    @Test
    public void intersectingConnectionsTest() throws Exception {
        //Tests two lines that intersect within the map

        map.addStation(name1, new Position(45, 45));
        map.addStation(name2, new Position(90, 90));
        map.addConnection(name1, name2);

        map.addStation(name3, new Position(90, 45));
        map.addStation(name4, new Position(45, 90));
        map.addConnection(name3, name4);

        Connection connectionOne = map.getConnection(name1, name2);
        Connection connectionTwo = map.getConnection(name3, name4);

        //Connection One and Two should intersect
        assertTrue("Connections intersecting should be true", connectionOne.intersect(connectionTwo));
        assertTrue("Connections intersecting should be true", connectionTwo.intersect(connectionOne));

        //Connection should not intersect itself
        assertFalse("Connection should not intersect itself", connectionOne.intersect(connectionOne));
        assertFalse("Connection should not intersect itself", connectionTwo.intersect(connectionTwo));

    }

    @Test
    public void nonIntersectingTestOne() throws Exception {
        //Tests two lines that are not parallel but do not intersect

        map.addStation(name1, new Position(10, 10));
        map.addStation(name2, new Position(40, 40));
        map.addConnection(name1, name2);

        map.addStation(name3, new Position(10, 5));
        map.addStation(name4, new Position(39, 39));
        map.addConnection(name3, name4);

        Connection connectionOne = map.getConnection(name1, name2);
        Connection connectionTwo = map.getConnection(name3, name4);

        //Connection One and Two should not intersect
        assertFalse("Connections intersecting should be false", connectionOne.intersect(connectionTwo));
        assertFalse("Connections intersecting should be false", connectionTwo.intersect(connectionOne));
    }

    @Test
    public void nonIntersectingTestTwo() throws Exception {
        //Tests two parallel lines

        map.addStation(name1, new Position(10, 40));
        map.addStation(name2, new Position(40, 40));
        map.addConnection(name1, name2);

        map.addStation(name3, new Position(5, 20));
        map.addStation(name4, new Position(20, 20));
        map.addConnection(name3, name4);

        Connection connectionOne = map.getConnection(name1, name2);
        Connection connectionTwo = map.getConnection(name3, name4);

        //Connection One and Two should not intersect
        assertFalse("Connections intersecting should be false", connectionOne.intersect(connectionTwo));
        assertFalse("Connections intersecting should be false", connectionTwo.intersect(connectionOne));
    }
}
