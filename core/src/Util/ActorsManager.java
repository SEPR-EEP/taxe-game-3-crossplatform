package Util;

import com.badlogic.gdx.scenes.scene2d.Actor;
import fvs.taxe.actor.GenericActor;
import fvs.taxe.actor.ObstacleActor;
import fvs.taxe.actor.TrainActor;
import sun.net.www.content.text.Generic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class can be used statically as an HashMap
 * <String, GenericActor> with the following methods:
 * - GenericActor Util.ActorsManager.get(String)
 * - Util.ActorsManager.put(String, GenericActor)
 *
 * Created by alfioemanuele on 3/2/15.
 */
public class ActorsManager {

    private static HashMap<String, GenericActor> actors = new HashMap<String, GenericActor>();
    private static List<GenericActor> trainActors = new ArrayList<GenericActor>();
    private static List<ObstacleActor> hiddenActors = new ArrayList<ObstacleActor>();

    public static GenericActor get(String s) {
        return ActorsManager.actors.get(s);
    }

    public static void put(String s, GenericActor g) {
        ActorsManager.actors.put(s, g);
    }
    public static boolean containsKey(String s) { return ActorsManager.actors.containsKey(s); }

    public static void addTrainActor(GenericActor a) {
        ActorsManager.trainActors.add(a);
    }

    public static List<GenericActor> getTrainActors() {
        return ActorsManager.trainActors;
    }

    public static void interruptAllTrains() {
        for ( GenericActor i: trainActors ) {
            TrainActor x = (TrainActor) i;
            x.setPosition(-1, -1);
            x.setVisible(false);
            x.clearActions();
        }
    }

    public static void hideAllObstacles() {
        hiddenActors.clear();
        for ( GenericActor a: actors.values() ) {
            if (a instanceof ObstacleActor) {
                if (((ObstacleActor) a).isVisible()) {
                    hiddenActors.add((ObstacleActor) a);
                    ((ObstacleActor) a).setVisible(false);
                }
            }
        }
    }

    public static void showAllObstacles() {
        for ( ObstacleActor a: hiddenActors ) {
            a.setVisible(true);
        }
    }

}
