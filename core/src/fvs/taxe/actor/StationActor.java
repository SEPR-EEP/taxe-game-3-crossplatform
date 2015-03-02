package fvs.taxe.actor;


import fvs.taxe.CustomTexture;
import gameLogic.map.IPositionable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

/**This class is a type of image specifically for creating Station actors.*/
public class StationActor extends Image {
	
	/**The amount to increase a StationActors's size (width and height) when it is selected  in pixels.*/
	private static final int STATION_SIZE_INCREASE = 10;
	
	/**The duration of time a StationActor can be selected for in seconds.*/
	private static final float SELECTION_DURATION = 3f;

	/**The width of the StationActor in pixels.*/
	private int width = 20;
	
	/**The height of the StationActor in pixels.*/
	private int height = 20;
	
	/**Whether the actor is currently selected. This prevents multiple clicks on the same station.*/
	private boolean selected = false;

	/**Instantiation method
	 * @param location the location of the station actor
	 */
	public StationActor(IPositionable location) {
		super(new CustomTexture(Gdx.files.internal("station_dot.png")));

		setSize(width, height);
		setPosition(location.getX() - width / 2, location.getY() - height / 2);
	}

	/**This method is called when the StationActor is selected. The StationActor's size is increased for the SELECTION_DURATION*/
	public void selected() {
		if (!selected){
			selected = true;
			setPosition(getX()-STATION_SIZE_INCREASE/2,getY()-STATION_SIZE_INCREASE/2);
			setSize(width+STATION_SIZE_INCREASE, height +STATION_SIZE_INCREASE);

			Timer.schedule(new Task(){
				@Override
				public void run() {
					setPosition(getX()+STATION_SIZE_INCREASE/2,getY()+STATION_SIZE_INCREASE/2);
					setSize(width, height);
					selected =false;
				}
			}, SELECTION_DURATION);
		} 
	}


}
