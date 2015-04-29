package gameLogic;


import fvs.taxe.controller.RouteController;

import java.util.HashMap;

public class RouteControllersCounter {
    private static HashMap<RouteController, Integer> h = new HashMap<RouteController, Integer>();
    public static int get(RouteController r) {
        return h.get(r);
    }
    public static void increment(RouteController r) {
        h.put(r, h.get(r) + 1);
    }
}
