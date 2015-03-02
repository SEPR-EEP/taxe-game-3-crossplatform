package fvs.taxe;

import gameLogic.map.Station;

/**This interface adds a clicked method for a specific station, allowing a Game to have an action for when a station has been clicked.*/
public interface StationClickListener {
    public void clicked(Station station);
}
